package com.zhangke.utopia.activitypub.app.internal.screen.server.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerTrendingViewModel @Inject constructor(
    private val getServerTrending: GetServerTrendingUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
) : ViewModel() {

    lateinit var baseUrl: String

    private var dataSource: ServerTrendingDataSource? = null

    val statusFlow = Pager(PagingConfig(pageSize = 40)) {
        ServerTrendingDataSource(baseUrl, getServerTrending, statusAdapter).also {
            dataSource = it
        }
    }.flow.cachedIn(viewModelScope)

    fun onRefresh() {
        dataSource?.invalidate()
    }
}
