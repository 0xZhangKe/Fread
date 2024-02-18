package com.zhangke.utopia.rss.internal.rss

import java.util.Date

data class RssChannel(
    val title: String,
    val link: String?,
    val description: String?,
    val image: RssImage?,
    val lastBuildDate: Date?,
    val updatePeriod: String?,
    val items: List<RssItem>,
)
