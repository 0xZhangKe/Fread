package com.zhangke.utopia.feeds.pages.home

import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.model.ContentConfig

data class ContentHomeUiState(
    val currentPageIndex: Int,
    val contentConfigList: List<ContentConfig>,
    val accountList: List<LoggedAccount>,
)
