package com.zhangke.framework.controller

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import kotlinx.coroutines.CoroutineScope

data class CommonLoadableUiState<T>(
    override val dataList: List<T>,
    override val refreshing: Boolean,
    override val loadMoreState: LoadState,
    override val errorMessage: TextString?,
) : LoadableUiState<T, CommonLoadableUiState<T>> {

    override fun copyObject(
        dataList: List<T>,
        refreshing: Boolean,
        loadMoreState: LoadState,
        errorMessage: TextString?
    ): CommonLoadableUiState<T> {
        return copy(
            dataList = dataList,
            refreshing = refreshing,
            loadMoreState = loadMoreState,
            errorMessage = errorMessage,
        )
    }
}

class CommonLoadableController<T>(
    coroutineScope: CoroutineScope,
) : LoadableController<T, CommonLoadableUiState<T>>(
    coroutineScope = coroutineScope,
    initialUiState = CommonLoadableUiState(
        dataList = emptyList(),
        refreshing = false,
        loadMoreState = LoadState.Idle,
        errorMessage = null,
    )
)
