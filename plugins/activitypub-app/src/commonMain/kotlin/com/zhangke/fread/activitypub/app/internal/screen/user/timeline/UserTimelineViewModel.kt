package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
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

class UserTimelineViewModel(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    private val statusEntityAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val clientManager: ActivityPubClientManager,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
    val tabType: UserTimelineTabType,
    val locator: PlatformLocator,
    val webFinger: WebFinger,
    val userId: String?,
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
            loadFirstPageLocalFeeds = {
                Result.success(emptyList())
            },
            loadNewFromServerFunction = ::loadNewDataFromServer,
            loadMoreFunction = ::loadMoreDataFromServer,
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private suspend fun loadNewDataFromServer(): Result<RefreshResult> {
        return loadUserTimeline(null)
            .map {
                RefreshResult(
                    newStatus = it,
                    deletedStatus = emptyList(),
                )
            }
    }

    private suspend fun loadMoreDataFromServer(maxId: String): Result<List<StatusUiState>> {
        return loadUserTimeline(maxId)
    }

    private suspend fun loadUserTimeline(maxId: String? = null): Result<List<StatusUiState>> {
        val loggedAccount = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        val accountId = if (userId.isNullOrEmpty()) {
            val accountIdResult =
                webFingerBaseUrlToUserIdRepo.getUserId(webFinger, locator)
            if (accountIdResult.isFailure) {
                return Result.failure(accountIdResult.exceptionOrNull()!!)
            }
            accountIdResult.getOrThrow()
        } else {
            userId
        }
        val platformResult = platformRepo.getPlatform(locator)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return fetchStatus(
            accountId = accountId,
            maxId = maxId,
        ).map { data ->
            data.filter { it.id != maxId }.map { item ->
                item.toUiState(loggedAccount, platform)
            }
        }
    }

    private suspend fun fetchStatus(
        accountId: String,
        maxId: String?,
    ): Result<List<ActivityPubStatusEntity>> {
        val showPinned = tabType == UserTimelineTabType.POSTS
        val pinnedStatus = mutableListOf<ActivityPubStatusEntity>()
        val accountRepo = clientManager.getClient(locator).accountRepo
        if (showPinned && maxId == null) {
            // first page
            val pinnedStatusResult = accountRepo.getStatuses(
                id = accountId,
                pinned = true,
                excludeReplies = true,
                onlyMedia = false,
            )
            if (pinnedStatusResult.isFailure) {
                return Result.failure(pinnedStatusResult.exceptionOrNull()!!)
            }
            pinnedStatus += pinnedStatusResult.getOrThrow().map { it.copy(pinned = true) }
        }
        return accountRepo.getStatuses(
            id = accountId,
            pinned = false,
            maxId = maxId,
            excludeReplies = tabType != UserTimelineTabType.REPLIES,
            onlyMedia = tabType == UserTimelineTabType.MEDIA,
        ).map {
            pinnedStatus + it
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(
        loggedAccount: ActivityPubLoggedAccount?,
        platform: BlogPlatform,
    ): StatusUiState {
        val status = statusEntityAdapter.toStatusUiState(
            entity = this,
            locator = locator,
            platform = platform,
            loggedAccount = loggedAccount,
        )
        status.status.preParseStatus()
        return status
    }
}
