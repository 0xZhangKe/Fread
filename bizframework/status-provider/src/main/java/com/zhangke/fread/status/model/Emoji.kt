package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val shortcode: String,
    val url: String,
    val staticUrl: String,
): java.io.Serializable
