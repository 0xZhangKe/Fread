package com.zhangke.fread.bluesky.internal.screen.search

import app.bsky.feed.SearchPostsQueryParams
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.screen.search.AbstractSearchStatusViewModel
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class SearchStatusViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    statusProvider: StatusProvider,
    statusUiStateAdapter: StatusUiStateAdapter,
    statusUpdater: StatusUpdater,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    private val platformRepo: BlueskyPlatformRepo,
    private val statusAdapter: BlueskyStatusAdapter,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val did: String,
) : AbstractSearchStatusViewModel(
    statusProvider = statusProvider,
    statusUiStateAdapter = statusUiStateAdapter,
    statusUpdater = statusUpdater,
    refactorToNewStatus = refactorToNewStatus,
) {

    fun interface Factory : ViewModelFactory {

        fun create(locator: PlatformLocator, did: String): SearchStatusViewModel
    }

    private var cursor: String? = null

    override suspend fun performSearch(
        query: String,
        loadMore: Boolean
    ): Result<List<StatusUiState>> {
        if (!loadMore) {
            cursor = null
        }
        if (loadMore && cursor == null) {
            return Result.success(emptyList())
        }
        val client = clientManager.getClient(locator)
        val account = client.loggedAccountProvider()
        val platform = platformRepo.getPlatform(client.baseUrl)
        val params = SearchPostsQueryParams(
            q = query,
            author = Did(did),
            cursor = cursor,
        )
        return client.searchPostsCatching(params)
            .onSuccess { cursor = it.cursor }
            .map { result ->
                result.posts.map {
                    statusAdapter.convertToUiState(
                        locator = locator,
                        postView = it,
                        platform = platform,
                        loggedAccount = account,
                    )
                }
            }
    }
}
