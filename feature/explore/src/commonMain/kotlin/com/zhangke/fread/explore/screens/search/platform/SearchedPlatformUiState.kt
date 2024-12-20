package com.zhangke.fread.explore.screens.search.platform

import com.zhangke.fread.status.search.SearchContentResult

data class SearchedPlatformUiState(
    val searchedList: List<SearchContentResult>,
    val searching: Boolean,
){

    companion object{

        fun default() = SearchedPlatformUiState(
            searchedList = emptyList(),
            searching = false,
        )
    }
}