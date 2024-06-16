package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.fread.status.author.BlogAuthor

data class ServerDetailContract(
    val email: String,
    val account: BlogAuthor,
)
