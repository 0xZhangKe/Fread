package com.zhangke.utopia.explore.screens.search.author

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel(assistedFactory = SearchAuthorViewModel.Factory::class)
open class SearchAuthorViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    @Assisted val baseUrl: FormalBaseUrl,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(baseUrl: FormalBaseUrl): SearchAuthorViewModel
    }

    private val loadableController = CommonLoadableController<BlogAuthor>(viewModelScope)
    val uiState: StateFlow<CommonLoadableUiState<BlogAuthor>> get() = loadableController.uiState

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    fun onRefresh(query: String) {
        loadableController.onRefresh {
            statusProvider.searchEngine.searchAuthor(baseUrl, query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore {
            statusProvider.searchEngine.searchAuthor(baseUrl, query, offset)
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        val route = statusProvider.screenProvider.getUserDetailRoute(blogAuthor.uri) ?: return
        launchInViewModel {
            _openScreenFlow.emit(route)
        }
    }
}
