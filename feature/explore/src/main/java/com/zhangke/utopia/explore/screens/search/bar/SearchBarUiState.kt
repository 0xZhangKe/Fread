package com.zhangke.utopia.explore.screens.search.bar

import com.zhangke.utopia.common.status.model.SearchResultUiState

data class SearchBarUiState(
    val query: String,
    val resultList: List<SearchResultUiState>,
)
