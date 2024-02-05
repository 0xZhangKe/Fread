package com.zhangke.utopia.explore.screens.search.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class SearchPlatformViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val loadableController = LoadableController<BlogPlatform>(viewModelScope)

    val uiState: StateFlow<LoadableUiState<BlogPlatform>> get() = loadableController.uiState

    fun onRefresh(query: String) {
        loadableController.refresh {
            statusProvider.searchEngine.searchPlatform(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.loadMore {
            statusProvider.searchEngine.searchPlatform(query, offset)
        }
    }
}
