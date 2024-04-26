package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.feeds.FeedsViewModelController
import com.zhangke.utopia.status.ui.feeds.InteractiveHandler
import dagger.assisted.AssistedInject

class UserTimelineViewModel @AssistedInject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    interactiveHandler: InteractiveHandler,
    val role: IdentityRole,
    val userUriInsights: UserUriInsights,
) : SubViewModel() {

    private val feedsViewModelController = FeedsViewModelController(
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
        loadFirstPageLocalFeeds = { Result.success(emptyList()) },
        loadNewFromServerFunction = ::loadNewFromServer,
        loadMoreFunction = ::loadMore,
        resolveRole = { role },
        onStatusUpdate = {},
    )

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return loadUserTimeline().map {
            RefreshResult(
                newStatus = it,
                deletedStatus = emptyList(),
            )
        }
    }

    private suspend fun loadMore(maxId: String?): Result<List<Status>> {
        return loadUserTimeline(maxId)
    }

    val uiState = feedsViewModelController.uiState
    val errorMessageFlow = feedsViewModelController.errorMessageFlow
    val openScreenFlow = feedsViewModelController.openScreenFlow
    val newStatusNotifyFlow = feedsViewModelController.newStatusNotifyFlow

    init {
        feedsViewModelController.initFeeds(false)
    }

    fun onRefresh() {
        feedsViewModelController.refresh()
    }

    fun onLoadMore() {
        feedsViewModelController.loadMore()
    }

    private suspend fun loadUserTimeline(maxId: String? = null): Result<List<Status>> {
        val accountIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, role)
        if (accountIdResult.isFailure) {
            return Result.failure(accountIdResult.exceptionOrNull()!!)
        }
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(role)
            .accountRepo
            .getStatuses(
                id = accountIdResult.getOrThrow(),
                maxId = maxId,
            ).map { it.map { item -> statusAdapter.toStatus(item, platform) } }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        feedsViewModelController.onInteractive(status, interaction)
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        feedsViewModelController.onVoted(status, options)
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        feedsViewModelController.onUserInfoClick(blogAuthor)
    }
}
