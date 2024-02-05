package com.zhangke.utopia.explore.screens.search.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.Hashtag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class SearchHashtagViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val loadableController = LoadableController<Hashtag>(viewModelScope)

    val uiState: StateFlow<LoadableUiState<Hashtag>> get() = loadableController.uiState

    fun onRefresh(query: String) {
        loadableController.refresh {
            statusProvider.searchEngine.searchHashtag(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.loadMore {
            statusProvider.searchEngine.searchHashtag(query, offset)
        }
    }
}
