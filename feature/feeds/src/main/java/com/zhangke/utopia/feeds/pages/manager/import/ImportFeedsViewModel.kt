package com.zhangke.utopia.feeds.pages.manager.import

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ImportFeedsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        ImportFeedsUiState(
            outputInfoList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {

    }
}
