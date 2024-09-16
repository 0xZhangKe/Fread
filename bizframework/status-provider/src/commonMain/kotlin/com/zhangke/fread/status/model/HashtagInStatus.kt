package com.zhangke.fread.status.model

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
data class HashtagInStatus(
    val name: String,
    val url: String,
    val protocol: StatusProviderProtocol,
): PlatformSerializable
