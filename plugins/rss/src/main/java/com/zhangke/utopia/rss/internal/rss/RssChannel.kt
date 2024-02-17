package com.zhangke.utopia.rss.internal.rss

data class RssChannel(
    val title: String?,
    val link: String?,
    val description: String?,
    val image: RssImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val items: List<RssItem>,
)
