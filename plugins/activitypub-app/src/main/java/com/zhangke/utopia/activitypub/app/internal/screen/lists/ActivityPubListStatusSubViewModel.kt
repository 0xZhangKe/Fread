package com.zhangke.utopia.activitypub.app.internal.screen.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status

class ActivityPubListStatusSubViewModel(
    platformRepo: ActivityPubPlatformRepo,
    private val listStatusRepo: ListStatusRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    statusAdapter: ActivityPubStatusAdapter,
    interactiveHandler: ActivityPubInteractiveHandler,
    private val serverBaseUrl: FormalBaseUrl,
    private val listId: String,
) : ViewModel() {

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
            baseUrl = serverBaseUrl,
            getStatusFromServer = ::getRemoteStatus,
            getStatusFromLocal = ::getLocalStatus,
        )
    }

    fun onRefresh() {
        loadableController.onRefresh(serverBaseUrl) {
            getRemoteStatus()
        }
    }

    fun onLoadMore() {
        loadableController.onLoadMore(serverBaseUrl) { maxId ->
            listStatusRepo.loadMore(
                serverBaseUrl = serverBaseUrl,
                listId = listId,
                maxId = maxId,
            )
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        loadableController.onInteractive(status, interaction)
    }

    private suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return listStatusRepo.getLocalStatus(
            serverBaseUrl = serverBaseUrl,
            listId = listId,
        )
    }

    private suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>> {
        return listStatusRepo.getRemoteStatus(
            serverBaseUrl = serverBaseUrl,
            listId = listId,
        )
    }

    private fun updateLocalStatus(status: ActivityPubStatusEntity) {
        launchInViewModel {
            listStatusRepo.updateEntity(status)
        }
    }
}
