package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.StateFlow

class TrendingStatusSubViewModel(
    private val clientManager: ActivityPubClientManager,
    statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    platformRepo: ActivityPubPlatformRepo,
    interactiveHandler: ActivityPubInteractiveHandler,
    pollAdapter: ActivityPubPollAdapter,
    private val role: IdentityRole,
) : SubViewModel() {

    private val loadableController = ActivityPubStatusLoadController(
        statusAdapter = statusAdapter,
        clientManager = clientManager,
        platformRepo = platformRepo,
        coroutineScope = viewModelScope,
        pollAdapter = pollAdapter,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
    )

    val uiState: StateFlow<CommonLoadableUiState<StatusUiState>> = loadableController.uiState

    val errorMessageFlow = loadableController.errorMessageFlow

    init {
        loadableController.initStatusData(
            role = role,
            getStatusFromServer = { getServerTrending(0, it) },
        )
    }

    fun onRefresh() {
        loadableController.onRefresh(role) {
            getServerTrending(0, it)
        }
    }

    fun onLoadMore() {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore(role) { _, role ->
            getServerTrending(offset, role)
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        loadableController.onInteractive(role, status, interaction)
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        loadableController.onVoted(role, status, options)
    }

    private suspend fun getServerTrending(offset: Int, role: IdentityRole): Result<List<ActivityPubStatusEntity>> {
        return clientManager.getClient(role)
            .instanceRepo
            .getTrendsStatuses(
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                offset = offset,
            )
    }
}
