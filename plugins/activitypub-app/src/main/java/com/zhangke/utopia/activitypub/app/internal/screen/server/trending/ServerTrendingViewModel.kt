package com.zhangke.utopia.activitypub.app.internal.screen.server.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ServerTrendingViewModel @Inject constructor(
    private val getServerTrending: GetServerTrendingUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl

    private var dataSource: ServerTrendingDataSource? = null

    val statusFlow = Pager(PagingConfig(pageSize = 40)) {
        ServerTrendingDataSource(
            baseUrl = baseUrl,
            getServerTrending = getServerTrending,
            getStatusSupportAction = getStatusSupportAction,
            statusAdapter = statusAdapter,
        ).also {
            dataSource = it
        }
    }.flow.cachedIn(viewModelScope)
        .map {
            it.map(buildStatusUiState::invoke)
        }

    fun onRefresh() {
        dataSource?.invalidate()
    }

    fun onInteractive(interaction: StatusUiInteraction) {

    }
}
