package com.zhangke.utopia.pages.sources.add.search

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.pages.feeds.shared.source.StatusSourceUiState

data class SearchSourceForAddUiState(
    val addedSourceUriList: List<String>,
    val searchedResult: LoadableState<List<StatusSourceUiState>>,
)
