package com.zhangke.utopia.status.model

import com.zhangke.framework.composable.TextString

data class Hashtag(
    val name: String,
    val url: String,
    val description: TextString,
    val history: History,
    val following: Boolean,
) {

    data class History(
        val history: List<Float>,
        val min: Float?,
        val max: Float?,
    )
}
