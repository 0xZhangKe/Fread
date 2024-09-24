package com.zhangke.fread.activitypub.app.internal.screen.trending

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status

class TrendingStatusSubViewModel(
    private val statusProvider: StatusProvider,
    private val clientManager: ActivityPubClientManager,
    statusUpdater: StatusUpdater,
    private val statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val role: IdentityRole,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = { role },
            loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private fun loadFirstPageLocalFeeds(): Result<List<Status>> {
        return Result.success(emptyList())
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return getServerTrending(0).map {
            RefreshResult(
                newStatus = it,
                deletedStatus = emptyList(),
                useOldData = false,
            )
        }
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        val offset = uiState.value.feeds.size
        if (offset == 0) return Result.success(emptyList())
        return getServerTrending(offset)
    }

    private suspend fun getServerTrending(
        offset: Int,
    ): Result<List<Status>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(role)
            .instanceRepo
            .getTrendsStatuses(
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                offset = offset,
            ).map { it.mapToStatus(platform) }
    }

    private suspend fun List<ActivityPubStatusEntity>.mapToStatus(platform: BlogPlatform): List<Status> {
        return map { statusAdapter.toStatus(it, platform) }
    }
}
