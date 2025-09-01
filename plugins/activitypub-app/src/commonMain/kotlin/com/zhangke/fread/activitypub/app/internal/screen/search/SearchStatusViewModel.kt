package com.zhangke.fread.activitypub.app.internal.screen.search

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.screen.search.AbstractSearchStatusViewModel
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class SearchStatusViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    statusProvider: StatusProvider,
    private val platformRepo: ActivityPubPlatformRepo,
    loggedAccountProvider: LoggedAccountProvider,
    statusUiStateAdapter: StatusUiStateAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    statusUpdater: StatusUpdater,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val userId: String,
) : AbstractSearchStatusViewModel(
    statusProvider = statusProvider,
    statusUiStateAdapter = statusUiStateAdapter,
    statusUpdater = statusUpdater,
    refactorToNewStatus = refactorToNewStatus,
) {

    fun interface Factory : ViewModelFactory {

        fun create(locator: PlatformLocator, userId: String): SearchStatusViewModel
    }

    private var platform: BlogPlatform? = null
    private var loggedAccount: ActivityPubLoggedAccount? = loggedAccountProvider.getAccount(locator)
    private var maxId: String? = null

    init {
        launchInViewModel { platform = loadPlatform().getOrNull() }
    }

    override suspend fun performSearch(
        query: String,
        loadMore: Boolean
    ): Result<List<StatusUiState>> {
        if (!loadMore) {
            this.maxId = null
        }
        val platformResult = loadPlatform()
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(locator)
            .searchRepo
            .queryStatus(
                query = query,
                accountId = userId,
                maxId = maxId,
                limit = 20,
            ).map { statuses ->
                statuses.map {
                    statusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        locator = locator,
                        loggedAccount = loggedAccount,
                    )
                }
            }.onSuccess {
                this.maxId = it.lastOrNull()?.status?.id
            }
    }

    private suspend fun loadPlatform(): Result<BlogPlatform> {
        if (platform != null) return Result.success(platform!!)
        return platformRepo.getPlatform(locator)
            .onSuccess { platform = it }
    }
}
