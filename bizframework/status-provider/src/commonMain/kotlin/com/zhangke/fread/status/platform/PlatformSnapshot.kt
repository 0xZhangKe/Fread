package com.zhangke.fread.status.platform

import com.zhangke.fread.status.model.StatusProviderProtocol

data class PlatformSnapshot (
    val domain: String,
    val description: String,
    val thumbnail: String,
    val protocol: StatusProviderProtocol,
    val priority: Int = 0,
)
