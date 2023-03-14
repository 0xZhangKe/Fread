package com.zhangke.utopia.pages.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _pageState = MutableStateFlow(PageState.ADD_SERVER)
    val pageState: StateFlow<PageState> get() = _pageState

    init {
        viewModelScope.launch {
        }
    }

    enum class PageState {

        ADD_SERVER,
        FEEDS;
    }
}