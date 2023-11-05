package com.zhangke.utopia.status.model

import com.zhangke.framework.utils.WebFinger

data class UtopiaAuthor (
    val name: String,
    val description: String,
    val webFinger: WebFinger,
    val platform: UtopiaPlatform,
    val avatar: String,
)
