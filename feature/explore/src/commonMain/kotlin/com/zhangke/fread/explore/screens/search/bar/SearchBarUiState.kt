package com.zhangke.fread.explore.screens.search.bar

import com.zhangke.fread.common.status.model.SearchResultUiState
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator

data class SearchBarUiState(
    val locator: PlatformLocator?,
    val account: LoggedAccount?,
    val query: String,
    val resultList: List<SearchResultUiState>,
)
