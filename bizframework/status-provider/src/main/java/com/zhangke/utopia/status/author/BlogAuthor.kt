package com.zhangke.utopia.status.author

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.uri.StatusProviderUri

open class BlogAuthor (
    val uri: StatusProviderUri,
    val webFinger: WebFinger,
    val name: String,
    val description: String,
    val avatar: String?,
)
