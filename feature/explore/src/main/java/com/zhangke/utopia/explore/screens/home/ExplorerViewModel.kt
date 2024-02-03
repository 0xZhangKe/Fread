package com.zhangke.utopia.explore.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ExplorerViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(ExplorerUiState())

}
