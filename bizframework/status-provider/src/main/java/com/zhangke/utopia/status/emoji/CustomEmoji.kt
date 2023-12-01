package com.zhangke.utopia.status.emoji

data class CustomEmoji(
    val shortcode: String,
    val url: String,
    val staticUrl: String,
    val visibleInPicker: Boolean,
    val category: String,
)
