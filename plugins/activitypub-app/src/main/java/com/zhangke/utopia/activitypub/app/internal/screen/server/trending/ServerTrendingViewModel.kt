package com.zhangke.utopia.activitypub.app.internal.screen.server.trending

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.zhangke.framework.composable.LoadableState
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ServerTrendingViewModel @Inject constructor(
    private val getServerTrending: GetServerTrendingUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl

    private var dataSource: ServerTrendingDataSource? = null

    private val _statusFlow =
        MutableStateFlow<LoadableState<Flow<PagingData<StatusUiState>>>>(LoadableState.Loading())

    val statusFlow: StateFlow<LoadableState<Flow<PagingData<StatusUiState>>>> = _statusFlow

    fun onPrepared() {
        if (_statusFlow.value is LoadableState.Loading) return
        launchInViewModel {
            Log.d("U_TEST", "start getting blog platform")
            val blogPlatformResult = platformRepo.getPlatform(baseUrl)
            Log.d("U_TEST", "blogPlatformResult is $blogPlatformResult")
            if (blogPlatformResult.isFailure) {
                _statusFlow.value = LoadableState.Failed(blogPlatformResult.exceptionOrNull()!!)
                return@launchInViewModel
            }
            _statusFlow.value =
                LoadableState.success(createStatusFlow(blogPlatformResult.getOrThrow()))
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
        }.flow.cachedIn(viewModelScope)
            .map {
                it.map(buildStatusUiState::invoke)
            }

    }

    fun onRefresh() {
        dataSource?.invalidate()
    }

    fun onInteractive(interaction: StatusUiInteraction) {

    }
}
