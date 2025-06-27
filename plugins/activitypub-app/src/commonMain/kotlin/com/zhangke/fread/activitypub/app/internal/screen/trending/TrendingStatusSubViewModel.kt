package com.zhangke.fread.activitypub.app.internal.screen.trending

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState

class TrendingStatusSubViewModel(
    private val statusProvider: StatusProvider,
    private val clientManager: ActivityPubClientManager,
    statusUpdater: StatusUpdater,
    private val statusAdapter: ActivityPubStatusAdapter,
    statusUiStateAdapter: StatusUiStateAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val locator: PlatformLocator,
    private val loggedAccountProvider: LoggedAccountProvider,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    init {
        initController(
            coroutineScope = viewModelScope,
            locatorResolver = { locator },
            loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private fun loadFirstPageLocalFeeds(): Result<List<StatusUiState>> {
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

    private suspend fun loadMore(maxId: String): Result<List<StatusUiState>> {
        val offset = uiState.value.feeds.size
        if (offset == 0) return Result.success(emptyList())
        return getServerTrending(offset)
    }

    private suspend fun getServerTrending(offset: Int): Result<List<StatusUiState>> {
        val platformResult = platformRepo.getPlatform(locator)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val loggedAccount = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        return clientManager.getClient(locator)
            .instanceRepo
            .getTrendsStatuses(
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                offset = offset,
            ).map { list ->
                list.map {
                    statusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        loggedAccount = loggedAccount,
                        locator = locator,
                    )
                }
            }
    }
}
