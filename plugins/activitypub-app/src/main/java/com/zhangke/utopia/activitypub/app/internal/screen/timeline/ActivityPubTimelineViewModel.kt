package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.screen.trending.ServerTrendingDataSource
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ActivityPubTimelineViewModel @Inject constructor(
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl
    lateinit var timelineType: TimelineSourceType

    private var dataSource: ServerTrendingDataSource? = null

    private val _statusFlow =
        MutableStateFlow<LoadableState<Flow<PagingData<StatusUiState>>>>(LoadableState.Idle())

    val statusFlow: StateFlow<LoadableState<Flow<PagingData<StatusUiState>>>> = _statusFlow

    fun onPrepared() {
        launchInViewModel {
            platformRepo.getPlatform(baseUrl)
                .onSuccess {

                }.onFailure {

                }
        }
    }

    fun onRefresh() {
        dataSource?.invalidate()
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
    }
}
