package com.zhangke.utopia.activitypubapp.model

import com.zhangke.framework.composable.TextString

data class ActivityPubTag(
    val name: String,
    val url: String,
    val description: TextString,
    val history: List<Float>,
    val following: Boolean,
)
