package com.zhangke.fread.status.platform

import com.zhangke.fread.status.model.StatusProviderProtocol

data class PlatformSnapshot(
    val uri: String? = null,
    val name: String? = null,
    val domain: String,
    val description: String,
    val thumbnail: String,
    val protocol: StatusProviderProtocol,
    val priority: Int = 0,
)
