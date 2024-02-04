package com.zhangke.utopia.explore.screens.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.status.StatusProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseSearchViewMode<T>(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    protected val mutableUiState = MutableStateFlow(
        SearchedResultUiState<T>(
            searching = false,
            resultList = emptyList(),
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState = mutableUiState.asStateFlow()

    init {

    }
}
