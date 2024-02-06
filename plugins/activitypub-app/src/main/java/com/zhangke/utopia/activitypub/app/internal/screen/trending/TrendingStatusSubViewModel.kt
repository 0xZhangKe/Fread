package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.StateFlow

class TrendingStatusSubViewModel(
    private val clientManager: ActivityPubClientManager,
    statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    platformRepo: ActivityPubPlatformRepo,
    interactiveHandler: ActivityPubInteractiveHandler,
    private val baseUrl: FormalBaseUrl,
) : SubViewModel() {

    private val loadableController = ActivityPubStatusLoadController(
        statusAdapter = statusAdapter,
        platformRepo = platformRepo,
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
    )

    val uiState: StateFlow<CommonLoadableUiState<StatusUiState>> = loadableController.uiState

    val errorMessageFlow = loadableController.errorMessageFlow

    init {
        loadableController.initStatusData(
            baseUrl = baseUrl,
            getStatusFromServer = { getServerTrending(0, it) },
        )
    }

    fun onRefresh() {
        loadableController.onRefresh(baseUrl) {
            getServerTrending(0, it)
        }
    }

    fun onLoadMore() {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore(baseUrl) { maxId, baseUrl ->
            getServerTrending(offset, baseUrl)
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
        loadableController.onInteractive(status, interaction)
    }

    private suspend fun getServerTrending(offset: Int, baseUrl: FormalBaseUrl): Result<List<ActivityPubStatusEntity>> {
        return clientManager.getClient(baseUrl)
            .instanceRepo
            .getTrendsStatuses(
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                offset = offset,
            )
    }
}
