package com.zhangke.fread.feeds.pages.home

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.nav.Tab
import com.zhangke.fread.status.model.FreadContent

data class ContentHomeUiState(
    val currentPageIndex: Int,
    val loading: Boolean,
    val contentAndTabList: List<Pair<FreadContent, Tab>>,
) {

    companion object {

        val default = ContentHomeUiState(
            currentPageIndex = 0,
            loading = true,
            contentAndTabList = emptyList(),
        )
    }
}
