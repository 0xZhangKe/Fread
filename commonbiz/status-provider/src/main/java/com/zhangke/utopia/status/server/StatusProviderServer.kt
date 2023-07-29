package com.zhangke.utopia.status.server

data class StatusProviderServer(
    val url: String,
    val name: String,
    val description: String?,
    val thumbnail: String?,
)
