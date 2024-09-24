package com.zhangke.fread.rss.internal.model

import java.util.Date

data class RssChannelItem(
    val id: String,
    val title: String,
    val author: String?,
    val link: String?,
    val pubDate: Date,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val categories: List<String>,
    val commentsUrl: String?,
)
