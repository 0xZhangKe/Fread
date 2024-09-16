package com.zhangke.fread.status.model

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val shortcode: String,
    val url: String,
    val staticUrl: String,
): PlatformSerializable
