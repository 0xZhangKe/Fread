package com.zhangke.utopia.pages.providermanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.blogprovider.BlogProviderManager
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.BlogSourceGroup
import com.zhangke.utopia.blogprovider.BlogSourceInterpreter
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
    private var blogSourceInterpreter: BlogSourceInterpreter? = null

    private val addedBlogSourceList = mutableListOf<BlogSource>()

    fun onSearchClick(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val interpreter = BlogProviderManager.sourceInterpreterList
                .firstOrNull { it.applicable(content) } ?: return@launch
            blogSourceGroup = interpreter.createSourceGroup(content)
            blogSourceInterpreter = interpreter
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
                    addedBlogSourceList.map { blogSourceInterpreter!!.validate(it) }
                        .reduce { acc, b -> acc && b }
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