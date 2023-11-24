package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import com.zhangke.utopia.status.account.LoggedAccount

data class PostStatusUiState(
    val account: LoggedAccount,
    val availableAccountList: List<LoggedAccount>,
    val content: String,
    val mediaPathList: List<Uri>,
    val sensitive: Boolean,
    val language: String,
) {

    val allowedSelectCount: Int get() = (4 - mediaPathList.size).coerceAtLeast(0)
}
