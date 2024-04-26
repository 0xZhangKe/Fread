package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.feeds.FeedsViewModelController
import com.zhangke.utopia.status.ui.feeds.InteractiveHandler

class ActivityPubTimelineSubViewModel(
    private val timelineStatusRepo: TimelineStatusRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    interactiveHandler: InteractiveHandler,
    private val role: IdentityRole,
    private val type: ActivityPubStatusSourceType,
) : SubViewModel() {

    private val feedsViewModelController = FeedsViewModelController(
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
        loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
        loadNewFromServerFunction = ::loadNewFromServer,
        loadMoreFunction = ::loadMore,
        resolveRole = ::resolveRole,
        onStatusUpdate = ::onStatusUpdate,
    )

    private suspend fun loadFirstPageLocalFeeds(): Result<List<Status>> {
        return timelineStatusRepo.getLocalStatus(
            role = role,
            type = type,
        ).let { list ->
            Result.success(list)
        }
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return timelineStatusRepo.refreshStatus(
            role = role,
            type = type,
        )
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        return timelineStatusRepo.loadMore(
            role = role,
            type = type,
            maxId = maxId,
        )
    }

    private fun resolveRole(author: BlogAuthor): IdentityRole {
        return role
    }

    private fun onStatusUpdate(status: Status) {
        launchInViewModel {
            timelineStatusRepo.updateStatus(role, status)
        }
    }

    val uiState = feedsViewModelController.uiState
    val errorMessageFlow = feedsViewModelController.errorMessageFlow
    val newStatusNotifyFlow = feedsViewModelController.newStatusNotifyFlow
    val openScreenFlow = feedsViewModelController.openScreenFlow

    init {
        feedsViewModelController.initFeeds(true)
        feedsViewModelController.startAutoFetchNewerFeeds()
    }

    fun onRefresh() {
        feedsViewModelController.refresh()
    }

    fun onLoadMore() {
        feedsViewModelController.loadMore()
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
