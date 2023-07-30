package com.zhangke.utopia.status.server

import com.zhangke.utopia.status.uri.StatusProviderUri

data class StatusProviderServer(
    val url: String,
    val uri: StatusProviderUri,
    val name: String,
    val description: String?,
    val thumbnail: String?,
)
