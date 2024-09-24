package com.zhangke.fread.feeds.pages.manager.add.pre

import com.zhangke.fread.status.search.SearchContentResult

data class PreAddFeedsUiState(
    val query: String,
    val allSearchedResult: List<SearchContentResult>,
    val searching: Boolean,
    val searchErrorMessage: String?,
    val loading: Boolean,
    val showLoginDialog: Boolean,
) {

    companion object {

        val default = PreAddFeedsUiState(
            query = "",
            allSearchedResult = emptyList(),
            loading = false,
            showLoginDialog = false,
            searching = false,
            searchErrorMessage = null,
        )
    }
}
