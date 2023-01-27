package com.zhangke.utopia.pages.providermanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.blogprovider.BlogProviderManager
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.BlogSourceGroup
import com.zhangke.utopia.blogprovider.db.BlogSourceRepo
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

    private var blogSourceGroup: BlogSourceGroup? = null

    private val addedBlogSourceList = mutableListOf<BlogSource>()

    fun onSearchClick(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            blogSourceGroup = BlogProviderManager.sourceResolverList
                .mapFirstOrNull { it.resolve(content) } ?: return@launch
            _pageState.emit(PageState.SOURCE_INFO)
        }
    }

    fun requireBlogSourceGroup(): BlogSourceGroup {
        return blogSourceGroup!!
    }

    fun moveToInitializedPage() {
        viewModelScope.launch {
            _pageState.emit(PageState.INITIALIZE)
        }
    }

    fun onAddSourceServer(blogSource: BlogSource) {
        addedBlogSourceList += blogSource
    }

    fun onConfirm() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allSourceIsValidate =
                    addedBlogSourceList.map { blogSource ->
                        BlogProviderManager.authorizerList
                            .first { it.applicable(blogSource) }
                            .checkAuthorizer(blogSource)
                    }.reduce { acc, b -> acc && b }
                if (!allSourceIsValidate) return@launch
                BlogSourceRepo.insertFeeds("Placeholder", addedBlogSourceList)
            } catch (e: Exception) {
                toast(e.message)
            }
        }
    }

    enum class PageState {

        INITIALIZE,
        SOURCE_INFO,
    }
}