package com.zhangke.fread.activitypub.app.internal.screen.user.status

import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.api.PagingResult
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.preParseStatus

class StatusListViewModel(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val locator: PlatformLocator,
    val type: StatusListType,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    private var nextMaxId: String? = null

    init {
        initController(
            coroutineScope = viewModelScope,
            locatorResolver = { locator },
            loadFirstPageLocalFeeds = {
                Result.success(emptyList())
            },
            loadNewFromServerFunction = ::loadNewDataFromServer,
            loadMoreFunction = { loadMoreDataFromServer() },
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private suspend fun loadNewDataFromServer(): Result<RefreshResult> {
        nextMaxId = null
        val accountRepo = clientManager.getClient(locator).accountRepo
        val platformResult = platformRepo.getPlatform(locator)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val loggedAccount = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        return fetchStatuses(accountRepo)
            .map { pagingResult ->
                nextMaxId = pagingResult.pagingInfo.nextMaxId
                RefreshResult(
                    newStatus = pagingResult.data.map { it.toUiState(loggedAccount, platform) },
                    deletedStatus = emptyList(),
                )
            }
    }

    private suspend fun loadMoreDataFromServer(): Result<List<StatusUiState>> {
        val nextMaxId = nextMaxId
        if (nextMaxId.isNullOrEmpty()) {
            return Result.success(emptyList())
        }
        val accountRepo = clientManager.getClient(locator).accountRepo
        val platformResult = platformRepo.getPlatform(locator)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val loggedAccount = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        return fetchStatuses(accountRepo, nextMaxId)
            .map { pagingResult ->
                this@StatusListViewModel.nextMaxId = pagingResult.pagingInfo.nextMaxId
                pagingResult.data.map { it.toUiState(loggedAccount, platform) }
            }
    }

    private suspend fun fetchStatuses(
        accountRepo: AccountsRepo,
        maxId: String? = null,
    ): Result<PagingResult<List<ActivityPubStatusEntity>>> {
        return if (type == StatusListType.FAVOURITES) {
            accountRepo.getFavourites(maxId = maxId)
        } else {
            accountRepo.getBookmarks(maxId = maxId)
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(
        loggedAccount: ActivityPubLoggedAccount?,
        platform: BlogPlatform,
    ): StatusUiState {
        val status = statusAdapter.toStatusUiState(
            entity = this,
            platform = platform,
            locator = locator,
            loggedAccount = loggedAccount,
        )
        status.status.preParseStatus()
        return status
    }
}
