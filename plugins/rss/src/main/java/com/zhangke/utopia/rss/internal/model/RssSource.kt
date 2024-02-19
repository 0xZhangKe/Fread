package com.zhangke.utopia.rss.internal.model

import java.util.Date

data class RssSource(
    val url: String,
    val title: String,
    val displayName: String,
    val addDate: Date,
    val lastUpdateDate: Date,
    val updatePeriod: String?,
    val description: String?,
    val thumbnail: String?,
)
