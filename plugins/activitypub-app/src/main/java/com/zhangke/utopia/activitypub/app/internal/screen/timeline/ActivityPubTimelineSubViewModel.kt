package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status

class ActivityPubTimelineSubViewModel(
    private val timelineStatusRepo: TimelineStatusRepo,
    platformRepo: ActivityPubPlatformRepo,
    statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    interactiveHandler: ActivityPubInteractiveHandler,
    private val baseUrl: FormalBaseUrl,
    private val type: ActivityPubStatusSourceType,
) : SubViewModel() {

    private val loadableController = ActivityPubStatusLoadController(
        statusAdapter = statusAdapter,
        platformRepo = platformRepo,
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
        updateStatus = ::updateLocalStatus,
    )

    val uiState = loadableController.uiState
    val errorMessageFlow = loadableController.errorMessageFlow

    init {
        loadableController.initStatusData(
            baseUrl = baseUrl,
            getStatusFromServer = ::getRemoteStatus,
            getStatusFromLocal = ::getLocalStatus,
        )
    }

    fun onRefresh() {
        loadableController.onRefresh(baseUrl) {
            getRemoteStatus()
        }
    }

    fun onLoadMore() {
        loadableController.onLoadMore(baseUrl) { maxId ->
            timelineStatusRepo.loadMore(
                serverBaseUrl = baseUrl,
                type = type,
                maxId = maxId,
            )
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        loadableController.onInteractive(status, interaction)
    }

    private suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return timelineStatusRepo.getLocalStatus(
            serverBaseUrl = baseUrl,
            type = type,
        )
    }

    private suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>> {
        return timelineStatusRepo.getRemoteStatus(
            serverBaseUrl = baseUrl,
            type = type,
        )
    }

    private fun updateLocalStatus(status: ActivityPubStatusEntity) {
        launchInViewModel {
            timelineStatusRepo.updateEntity(status)
        }
    }
}
