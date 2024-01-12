package com.zhangke.utopia.activitypub.app.internal.screen.trending

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TrendingStatusViewModel @Inject constructor(
    private val getServerTrending: GetServerTrendingUseCase,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl

    private var dataSource: ServerTrendingDataSource? = null

    private val _statusFlow =
        MutableStateFlow<LoadableState<Flow<PagingData<StatusUiState>>>>(LoadableState.Idle())

    val statusFlow: StateFlow<LoadableState<Flow<PagingData<StatusUiState>>>> = _statusFlow

    init {
        Log.d("U_TEST", "TrendingStatusViewModel@${hashCode()} init")
    }

    fun onPrepared() {
        Log.d("U_TEST", "TrendingStatusViewModel@${hashCode()} onPrepared")
        launchInViewModel {
            _statusFlow.updateToLoading()
            platformRepo.getPlatform(baseUrl)
                .onSuccess { blogPlatform ->
                    val flow = createStatusFlow(blogPlatform)
                    _statusFlow.updateToSuccess(flow)
                }.onFailure { e ->
                    _statusFlow.updateToFailed(e)
                }
        }
    }

    private fun createStatusFlow(blogPlatform: BlogPlatform): Flow<PagingData<StatusUiState>> {
        return Pager(PagingConfig(pageSize = 40)) {
            ServerTrendingDataSource(
                baseUrl = baseUrl,
                getServerTrending = getServerTrending,
                getStatusSupportAction = getStatusSupportAction,
                statusAdapter = statusAdapter,
                platform = blogPlatform,
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
