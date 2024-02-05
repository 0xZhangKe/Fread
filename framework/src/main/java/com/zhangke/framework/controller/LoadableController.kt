package com.zhangke.framework.controller

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.utils.LoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface LoadableUiState<T> {

    val dataList: List<T>

    val refreshing: Boolean

    val loadMoreState: LoadState

    val errorMessage: TextString?

    fun <S: LoadableUiState<T>> copyObject(
        dataList: List<T> = this.dataList,
        refreshing: Boolean = this.refreshing,
        loadMoreState: LoadState = this.loadMoreState,
        errorMessage: TextString? = this.errorMessage,
    ): S
}

open class LoadableController<S, T: LoadableUiState<S>>(
    private val coroutineScope: CoroutineScope,
    initialUiState: T,
) {

    val mutableUiState: MutableStateFlow<T> = MutableStateFlow(initialUiState)
    val uiState = mutableUiState.asStateFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    fun refresh(
        refreshFunction: suspend () -> Result<List<S>>,
    ) {
        if (mutableUiState.value.refreshing) return
        mutableUiState.update {
            it.copyObject(refreshing = true, errorMessage = null)
        }
        refreshJob?.cancel()
        refreshJob = coroutineScope.launch {
            refreshFunction()
                .onSuccess { list ->
                    mutableUiState.update {
                        it.copyObject(
                            dataList = list,
                            refreshing = false,
                        )
                    }
                }.onFailure { e ->
                    val errorMessage = e.message?.let { textOf(it) }
                    mutableUiState.update {
                        it.copyObject(
                            errorMessage = errorMessage,
                            refreshing = false,
                        )
                    }
                }
        }
    }

    fun loadMore(
        loadMoreFunction: suspend () -> Result<List<S>>,
    ) {
        if (mutableUiState.value.refreshing) return
        if (mutableUiState.value.loadMoreState == LoadState.Loading) return
        mutableUiState.update { it.copyObject(loadMoreState = LoadState.Loading) }
        loadMoreJob?.cancel()
        loadMoreJob = coroutineScope.launch {
            loadMoreFunction()
                .onSuccess { list ->
                    mutableUiState.update {
                        it.copyObject(
                            dataList = it.dataList + list,
                            loadMoreState = LoadState.Idle,
                        )
                    }
                }.onFailure { e ->
                    mutableUiState.update {
                        it.copyObject(loadMoreState = LoadState.Failed(e))
                    }
                }
        }
    }
}
