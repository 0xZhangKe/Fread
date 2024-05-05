package com.zhangke.utopia.status.model

import kotlinx.serialization.Serializable

@Serializable
data class HashtagInStatus(
    val name: String,
    val url: String,
    val protocol: StatusProviderProtocol,
)
