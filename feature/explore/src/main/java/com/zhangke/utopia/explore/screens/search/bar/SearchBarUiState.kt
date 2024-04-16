package com.zhangke.utopia.explore.screens.search.bar

import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.status.model.IdentityRole

data class SearchBarUiState(
    val role: IdentityRole,
    val query: String,
    val resultList: List<SearchResultUiState>,
)
