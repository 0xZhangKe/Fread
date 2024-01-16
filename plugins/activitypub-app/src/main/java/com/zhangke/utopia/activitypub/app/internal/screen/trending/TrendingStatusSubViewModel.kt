package com.zhangke.utopia.activitypub.app.internal.screen.trending

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class TrendingStatusSubViewModel(
    private val getServerTrending: GetServerTrendingUseCase,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val baseUrl: FormalBaseUrl,
) : SubViewModel() {

    private var dataSource: ServerTrendingDataSource? = null

    private val _statusFlow = MutableStateFlow(createStatusFlow())

    val statusFlow: StateFlow<Flow<PagingData<StatusUiState>>> = _statusFlow

    private fun createStatusFlow(): Flow<PagingData<StatusUiState>> {
        return Pager(PagingConfig(pageSize = 40)) {
            ServerTrendingDataSource(
                baseUrl = baseUrl,
                getServerTrending = getServerTrending,
                getStatusSupportAction = getStatusSupportAction,
                statusAdapter = statusAdapter,
                platformRepo = platformRepo,
            ).also {
                dataSource = it
            }
        }.flow
            .cachedIn(viewModelScope)
            .map { it.map(buildStatusUiState::invoke) }
    }

    fun onRefresh() {
        dataSource?.invalidate()
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
    }
}
