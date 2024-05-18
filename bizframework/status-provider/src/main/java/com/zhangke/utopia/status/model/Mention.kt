package com.zhangke.utopia.status.model

import com.zhangke.framework.utils.WebFinger
import kotlinx.serialization.Serializable

@Serializable
data class Mention(
    val id: String,
    val username: String,
    val url: String,
    val webFinger: WebFinger,
    val protocol: StatusProviderProtocol,
): java.io.Serializable
