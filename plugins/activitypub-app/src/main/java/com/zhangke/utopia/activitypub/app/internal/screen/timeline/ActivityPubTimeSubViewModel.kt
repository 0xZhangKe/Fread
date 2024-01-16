package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class ActivityPubTimeSubViewModel(
    private val getTimelineStatus: GetTimelineStatusUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private var baseUrl: FormalBaseUrl,
    private var timelineType: TimelineSourceType,
) : SubViewModel() {

    private var dataSource: TimelineDataSource? = null

    private val _statusFlow = MutableStateFlow(createTimelineFlow())

    val statusFlow: StateFlow<Flow<PagingData<StatusUiState>>> = _statusFlow

    private fun createTimelineFlow(): Flow<PagingData<StatusUiState>> {
        return Pager(PagingConfig(pageSize = 40)) {
            TimelineDataSource(
                baseUrl = baseUrl,
                timelineSourceType = timelineType,
                getTimelineStatus = getTimelineStatus,
            ).also {
                dataSource = it
            }
        }.flow.cachedIn(viewModelScope).map { it.map(buildStatusUiState::invoke) }
    }

    fun onRefresh() {
        dataSource?.invalidate()
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
    }
}
