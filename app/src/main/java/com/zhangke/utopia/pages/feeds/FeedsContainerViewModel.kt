package com.zhangke.utopia.pages.feeds

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedsContainerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<FeedsContainerUiState> = _uiState.asStateFlow()

    fun onPageChanged(index: Int) {

    }

    private fun initialState(): FeedsContainerUiState {
        return FeedsContainerUiState(
            channelList = emptyList(),
            tabIndex = -1,
        )
    }
}