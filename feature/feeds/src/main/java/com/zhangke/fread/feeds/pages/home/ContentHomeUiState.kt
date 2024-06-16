package com.zhangke.fread.feeds.pages.home

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.ContentConfig

data class ContentHomeUiState(
    val currentPageIndex: Int,
    val loading: Boolean,
    val contentConfigList: List<ContentConfig>,
    val accountList: List<LoggedAccount>,
) {

    companion object {

        val default = ContentHomeUiState(
            currentPageIndex = 0,
            loading = true,
            contentConfigList = emptyList(),
            accountList = emptyList(),
        )
    }
}
