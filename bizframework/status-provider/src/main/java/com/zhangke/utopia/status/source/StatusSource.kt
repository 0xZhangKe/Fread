package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.uri.FormalUri

data class StatusSource(
    val uri: FormalUri,
    val name: String,
    val description: String,
    val thumbnail: String?,
)
