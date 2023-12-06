package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.uri.StatusProviderUri

data class StatusSource(
    val uri: StatusProviderUri,
    val name: String,
    val description: String,
    val thumbnail: String?,
)
