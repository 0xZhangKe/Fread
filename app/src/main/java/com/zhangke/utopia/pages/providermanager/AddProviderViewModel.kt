package com.zhangke.utopia.pages.providermanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.utils.RegexFactory
import com.zhangke.utopia.activitypubapp.servers.ActivityPubServers
import com.zhangke.utopia.blogprovider.BlogProviderManager
import com.zhangke.utopia.blogprovider.BlogSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddProviderViewModel : ViewModel() {

    private val _pageState = MutableStateFlow(PageState.INITIALIZE)

    /**
     * - 0: Initialized page.
     * - 1: Blog source info.
     */
    val pageState: StateFlow<PageState> get() = _pageState

    private var blogSource: BlogSource? = null

    fun onAddClick(content: String) {
        viewModelScope.launch {
            blogSource = BlogProviderManager.sourceFactoryList
                .mapFirstOrNull { it.tryCreateSource(content) } ?: return@launch
            _pageState.emit(PageState.SOURCE_INFO)
        }
    }

    fun requireActivityPubInstance(): BlogSource {
        return blogSource!!
    }

    fun moveToInitializedPage() {
        viewModelScope.launch {
            _pageState.emit(PageState.INITIALIZE)
        }
    }

    fun onAddActivityPubServer() {

    }

    enum class PageState {

        INITIALIZE,
        SOURCE_INFO,
    }
}