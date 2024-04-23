package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
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
    clientManager: ActivityPubClientManager,
    private val timelineStatusRepo: TimelineStatusRepo,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    interactiveHandler: InteractiveHandler,
    pollAdapter: ActivityPubPollAdapter,
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
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return timelineStatusRepo.getLocalStatus(
            role = role,
            type = type,
        ).let { list ->
            Result.success(list.map { statusAdapter.toStatus(it, platform) })
        }
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return timelineStatusRepo.refreshStatus(
            role = role,
            type = type,
        ).map { result ->
            RefreshResult(
                newStatus = result.newStatus.map { statusAdapter.toStatus(it, platform) },
                deletedStatus = result.deletedStatus.map { statusAdapter.toStatus(it, platform) },
            )
        }
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return timelineStatusRepo.loadMore(
            role = role,
            type = type,
            maxId = maxId,
        ).map { list ->
            list.map { statusAdapter.toStatus(it, platform) }
        }
    }

    private fun resolveRole(author: BlogAuthor): IdentityRole {
        return role
    }

    private fun onStatusUpdate(status: Status){
        launchInViewModel {
            timelineStatusRepo.updateEntity(statusAdapter.toStatus(status))
        }
    }

    private val loadableController = ActivityPubStatusLoadController(
        statusAdapter = statusAdapter,
        platformRepo = platformRepo,
        clientManager = clientManager,
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
        pollAdapter = pollAdapter,
        updateStatus = ::updateLocalStatus,
        updatePoll = { status, poll ->
            launchInViewModel {
                timelineStatusRepo.updatePoll(status.id, poll)
            }
        }
    )

    val uiState = loadableController.uiState
    val errorMessageFlow = loadableController.errorMessageFlow

    init {
        loadableController.initStatusData(
            role = role,
            getStatusFromServer = ::getRemoteStatus,
            getStatusFromLocal = ::getLocalStatus,
        )
    }

    fun onRefresh() {
        loadableController.onRefresh(role) {
            getRemoteStatus(it)
        }
    }

    fun onLoadMore() {
        loadableController.onLoadMore(role) { maxId, role ->
            timelineStatusRepo.loadMore(
                role = role,
                type = type,
                maxId = maxId,
            )
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        loadableController.onInteractive(role, status, interaction)
    }

    private suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return timelineStatusRepo.getLocalStatus(
            role = role,
            type = type,
        )
    }

    private suspend fun getRemoteStatus(role: IdentityRole): Result<List<ActivityPubStatusEntity>> {
        return timelineStatusRepo.getRemoteStatus(
            role = role,
            type = type,
        )
    }

    private fun updateLocalStatus(status: ActivityPubStatusEntity) {
        launchInViewModel {
            timelineStatusRepo.updateEntity(status)
        }
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        loadableController.onVoted(role, status, options)
    }
}
