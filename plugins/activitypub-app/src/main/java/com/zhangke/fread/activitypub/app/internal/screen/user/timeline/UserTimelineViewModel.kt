package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.preParseRichText
import com.zhangke.fread.status.status.model.Status

class UserTimelineViewModel(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    val tabType: UserTimelineTabType,
    val role: IdentityRole,
    val webFinger: WebFinger,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = { role },
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

    private suspend fun loadMoreDataFromServer(maxId: String): Result<List<Status>> {
        return loadUserTimeline(maxId)
    }

    private suspend fun loadUserTimeline(maxId: String? = null): Result<List<Status>> {
        val accountIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(webFinger, role)
        if (accountIdResult.isFailure) {
            return Result.failure(accountIdResult.exceptionOrNull()!!)
        }
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return fetchStatus(
            accountId = accountIdResult.getOrThrow(),
            maxId = maxId,
        ).map { data ->
            data.filter { it.id != maxId }.map { item -> item.toUiState(platform) }
        }
    }

    private suspend fun fetchStatus(
        accountId: String,
        maxId: String?,
    ): Result<List<ActivityPubStatusEntity>> {
        val showPinned = tabType == UserTimelineTabType.POSTS
        val pinnedStatus = mutableListOf<ActivityPubStatusEntity>()
        val accountRepo = clientManager.getClient(role).accountRepo
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

    private suspend fun ActivityPubStatusEntity.toUiState(platform: BlogPlatform): Status {
        val status = statusAdapter.toStatus(this, platform)
        status.preParseRichText()
        return status
    }
}
