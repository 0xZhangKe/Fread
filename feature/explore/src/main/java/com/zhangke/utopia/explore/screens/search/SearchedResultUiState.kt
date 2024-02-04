package com.zhangke.utopia.explore.screens.search

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState

data class SearchedResultUiState<T>(
    val searching: Boolean,
    val resultList: List<T>,
    val loadMoreState: LoadState,
    val errorMessage: TextString?,
)