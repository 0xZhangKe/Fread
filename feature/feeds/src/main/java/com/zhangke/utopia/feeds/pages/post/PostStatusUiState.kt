package com.zhangke.utopia.feeds.pages.post

import com.zhangke.utopia.status.account.LoggedAccount

data class PostStatusUiState(
    val account: LoggedAccount,
    val content: String,
    val imagePathList: List<String>,
    val sensitive: Boolean,
    val language: String,
)
