package com.zhangke.fread.feeds.pages.manager.search

import com.zhangke.fread.feeds.composable.StatusSourceUiState

data class SearchForAddUiState(
    val query: String,
    val searching: Boolean,
    val searchedList: List<StatusSourceUiState>,
) {

    companion object {

        fun default(): SearchForAddUiState {
            return SearchForAddUiState(
                query = "",
                searching = false,
                searchedList = emptyList(),
            )
        }
    }
}
