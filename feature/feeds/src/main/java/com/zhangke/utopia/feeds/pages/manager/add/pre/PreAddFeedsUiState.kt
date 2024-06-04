package com.zhangke.utopia.feeds.pages.manager.add.pre

import com.zhangke.utopia.status.search.SearchContentResult

data class PreAddFeedsUiState(
    val query: String,
    val allSearchedResult: List<SearchContentResult>,
    val loading: Boolean,
    val showLoginDialog: Boolean,
) {

    companion object {

        val default = PreAddFeedsUiState(
            query = "",
            allSearchedResult = emptyList(),
            loading = false,
            showLoginDialog = false,
        )
    }
}
