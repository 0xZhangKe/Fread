package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.composable.TextString

data class ActivityPubTag(
    val name: String,
    val url: String,
    val description: TextString,
    val following: Boolean,
    val history: ActivityPubTagHistory,
)

data class ActivityPubTagHistory(
    val history: List<Float>,
    val min: Float?,
    val max: Float?,
)
