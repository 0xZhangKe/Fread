package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.feeds.FeedsViewModelController
import com.zhangke.utopia.status.ui.feeds.InteractiveHandler

class TrendingStatusSubViewModel(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    interactiveHandler: InteractiveHandler,
    private val role: IdentityRole,
) : SubViewModel() {

    private val feedsViewModelController = FeedsViewModelController(
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
        loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
        loadNewFromServerFunction = ::loadNewFromServer,
        loadMoreFunction = ::loadMore,
        resolveRole = ::resolveRole,
        onStatusUpdate = {},
    )

    private fun loadFirstPageLocalFeeds(): Result<List<Status>> {
        return Result.success(emptyList())
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return getServerTrending(0).map {
            RefreshResult(
                newStatus = it,
                deletedStatus = emptyList(),
            )
        }
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        val offset = uiState.value.feeds.size
        if (offset == 0) return Result.success(emptyList())
        return getServerTrending(offset)
    }

    private fun resolveRole(author: BlogAuthor): IdentityRole {
        return role
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
        val offset = uiState.value.feeds.size
        if (offset == 0) return
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
