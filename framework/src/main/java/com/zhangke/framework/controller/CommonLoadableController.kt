package com.zhangke.framework.controller

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import kotlinx.coroutines.CoroutineScope

data class CommonLoadableUiState<T>(
    override val dataList: List<T>,
    override val refreshing: Boolean,
    override val loadMoreState: LoadState,
    override val errorMessage: TextString?,
) : LoadableUiState<T> {

    override fun <S : LoadableUiState<T>> copyObject(
        dataList: List<T>,
        refreshing: Boolean,
        loadMoreState: LoadState,
        errorMessage: TextString?,
    ): S {
        val coped = copy(
            dataList = dataList,
            refreshing = refreshing,
            loadMoreState = loadMoreState,
            errorMessage = errorMessage,
        )
        return coped as S
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
