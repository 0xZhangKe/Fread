package com.zhangke.fread.activitypub.app.internal.model

data class CustomEmoji(
    val shortcode: String,
    val url: String,
    val staticUrl: String,
    val visibleInPicker: Boolean,
    val category: String,
)
