package com.zhangke.fread.status.model

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor

data class LoggedAccountDetail(
    val account: LoggedAccount,
    val author: BlogAuthor,
)
