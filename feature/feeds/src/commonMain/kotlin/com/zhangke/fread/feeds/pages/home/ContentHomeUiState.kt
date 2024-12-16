package com.zhangke.fread.feeds.pages.home

import com.zhangke.fread.status.model.FreadContent

data class ContentHomeUiState(
    val currentPageIndex: Int,
    val loading: Boolean,
    val contentConfigList: List<FreadContent>,
) {

    companion object {

        val default = ContentHomeUiState(
            currentPageIndex = 0,
            loading = true,
            contentConfigList = emptyList(),
        )
    }
}
