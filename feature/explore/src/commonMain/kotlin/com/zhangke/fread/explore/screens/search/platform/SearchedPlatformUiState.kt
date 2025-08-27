package com.zhangke.fread.explore.screens.search.platform

import com.zhangke.fread.status.search.SearchedPlatform

data class SearchedPlatformUiState(
    val searchedList: List<SearchedPlatform>,
    val searching: Boolean,
){

    companion object{

        fun default() = SearchedPlatformUiState(
            searchedList = emptyList(),
            searching = false,
        )
    }
}