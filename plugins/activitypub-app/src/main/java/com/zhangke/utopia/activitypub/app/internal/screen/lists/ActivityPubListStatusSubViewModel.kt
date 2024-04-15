package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status

class ActivityPubListStatusSubViewModel(
    platformRepo: ActivityPubPlatformRepo,
    clientManager: ActivityPubClientManager,
    private val listStatusRepo: ListStatusRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    statusAdapter: ActivityPubStatusAdapter,
    interactiveHandler: ActivityPubInteractiveHandler,
    pollAdapter: ActivityPubPollAdapter,
    private val role: IdentityRole,
    private val listId: String,
) : SubViewModel() {

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
                listStatusRepo.updatePoll(status.id, poll)
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
            listStatusRepo.loadMore(
                role = role,
                listId = listId,
                maxId = maxId,
            )
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        loadableController.onInteractive(role, status, interaction)
    }

    private suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return listStatusRepo.getLocalStatus(
            role = role,
            listId = listId,
        )
    }

    private suspend fun getRemoteStatus(role: IdentityRole): Result<List<ActivityPubStatusEntity>> {
        return listStatusRepo.getRemoteStatus(
            role = role,
            listId = listId,
        )
    }

    private fun updateLocalStatus(status: ActivityPubStatusEntity) {
        launchInViewModel {
            listStatusRepo.updateEntity(status)
        }
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        loadableController.onVoted(role, status, options)
    }

}
