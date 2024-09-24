package com.zhangke.fread.rss.internal.rss

import kotlinx.datetime.Instant

data class RssChannel(
    val title: String,
    val link: String?,
    val description: String?,
    val image: RssImage?,
    val lastBuildDate: Instant?,
    val updatePeriod: String?,
    val items: List<RssItem>,
)
