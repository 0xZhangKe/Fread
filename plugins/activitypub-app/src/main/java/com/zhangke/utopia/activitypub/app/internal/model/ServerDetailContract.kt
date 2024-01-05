package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.utopia.status.author.BlogAuthor

data class ServerDetailContract(
    val email: String,
    val account: BlogAuthor,
)
