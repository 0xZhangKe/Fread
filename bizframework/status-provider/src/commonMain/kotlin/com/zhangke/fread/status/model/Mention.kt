package com.zhangke.fread.status.model

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.WebFinger
import kotlinx.serialization.Serializable

@Serializable
data class Mention(
    val id: String,
    val username: String,
    val url: String,
    val webFinger: WebFinger,
    val protocol: StatusProviderProtocol,
): PlatformSerializable
