package com.zhangke.utopia.pages.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.status_provider.BlogFeedsShell
import com.zhangke.utopia.status_provider.db.BlogSourceRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _pageState = MutableStateFlow(PageState.ADD_SERVER)
    val pageState: StateFlow<PageState> get() = _pageState

    private val _feedsShellFlow = MutableSharedFlow<List<BlogFeedsShell>>()
    val feedsShellFlow: SharedFlow<List<BlogFeedsShell>> get() = _feedsShellFlow

    init {
        viewModelScope.launch {
            val feedsShell = BlogSourceRepo.queryAllFeedsShell()
            if (feedsShell.isEmpty()) {
                _pageState.emit(PageState.ADD_SERVER)
            } else {
                _feedsShellFlow.emit(feedsShell)
                _pageState.emit(PageState.FEEDS)
            }
        }
    }

    enum class PageState {

        ADD_SERVER,
        FEEDS;
    }
}