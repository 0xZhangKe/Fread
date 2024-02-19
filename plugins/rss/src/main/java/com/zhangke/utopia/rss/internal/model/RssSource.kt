package com.zhangke.utopia.rss.internal.model

data class RssSource(
    val url: String,
    val title: String,
    val description: String?,
    val thumbnail: String?,
)
