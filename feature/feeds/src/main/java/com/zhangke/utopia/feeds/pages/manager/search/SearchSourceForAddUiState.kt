package com.zhangke.utopia.feeds.pages.manager.search

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.feeds.composable.StatusSourceUiState

internal data class SearchSourceForAddUiState(
    val addedSourceUriList: List<String>,
    val searchedResult: LoadableState<List<StatusSourceUiState>>,
)
