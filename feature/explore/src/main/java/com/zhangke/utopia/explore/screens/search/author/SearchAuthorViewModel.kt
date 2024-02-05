package com.zhangke.utopia.explore.screens.search.author

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class SearchAuthorViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val loadableController = LoadableController<BlogAuthor>(viewModelScope)
    val uiState: StateFlow<LoadableUiState<BlogAuthor>> get() = loadableController.uiState

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    fun onRefresh(query: String) {
        loadableController.refresh {
            statusProvider.searchEngine.searchAuthor(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.loadMore {
            statusProvider.searchEngine.searchAuthor(query, offset)
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        val route = statusProvider.screenProvider.getUserDetailRoute(blogAuthor.uri) ?: return
        launchInViewModel {
            _openScreenFlow.emit(route)
        }
    }
}
