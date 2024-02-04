package com.zhangke.utopia.explore.screens.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseSearchViewMode<T> : ViewModel() {

    protected val mutableUiState = MutableStateFlow(
        SearchedResultUiState<T>(
            searching = false,
            resultList = emptyList(),
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState = mutableUiState.asStateFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    protected fun refresh(
        refreshFunction: suspend () -> Result<List<T>>,
    ) {
        if (mutableUiState.value.searching) return
        mutableUiState.update { it.copy(searching = true, errorMessage = null) }
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            refreshFunction().onSuccess { list ->
                mutableUiState.update {
                    it.copy(
                        searching = false,
                        resultList = list,
                    )
                }
            }.onFailure { e ->
                mutableUiState.update {
                    it.copy(
                        searching = false,
                        errorMessage = e.message?.let { m -> textOf(m) }
                    )
                }
            }
        }
    }

    protected fun loadMore(loadMoreFunction: suspend () -> Result<List<T>>) {
        if (mutableUiState.value.searching) return
        if (mutableUiState.value.loadMoreState == LoadState.Loading) return
        mutableUiState.update { it.copy(loadMoreState = LoadState.Loading) }
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            loadMoreFunction().onSuccess { list ->
                mutableUiState.update {
                    it.copy(
                        resultList = it.resultList + list,
                        loadMoreState = LoadState.Idle,
                    )
                }
            }.onFailure { e ->
                mutableUiState.update {
                    it.copy(loadMoreState = LoadState.Failed(e))
                }
            }
        }
    }
}
