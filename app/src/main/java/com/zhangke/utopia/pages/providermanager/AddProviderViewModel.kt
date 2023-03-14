package com.zhangke.utopia.pages.providermanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceMaintainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddProviderViewModel : ViewModel() {

    private val _pageState = MutableStateFlow(PageState.INITIALIZE)

    /**
     * - 0: Initialized(Add server) page state.
     * - 1: Blog source info.
     */
    val pageState: StateFlow<PageState> get() = _pageState

    private var statusSourceMaintainer: StatusSourceMaintainer? = null

    private val addedBlogSourceList = mutableListOf<StatusSource>()

    fun onSearchClick(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
//            statusSourceMaintainer = BlogProviderManager.sourceResolverList
//                .mapFirstOrNull { it.resolve(content) } ?: return@launch
            _pageState.emit(PageState.SOURCE_INFO)
        }
    }

    fun moveToInitializedPage() {
        viewModelScope.launch {
            _pageState.emit(PageState.INITIALIZE)
        }
    }

    fun onAddSourceServer(blogSource: StatusSource) {
        addedBlogSourceList += blogSource
    }

    fun onConfirm() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    enum class PageState {

        INITIALIZE,
        SOURCE_INFO,
    }
}