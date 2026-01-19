@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.rss.internal.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

data class RssSource(
    val url: String,
    val homePage: String?,
    val title: String,
    val displayName: String,
    val addDate: Instant,
    val lastUpdateDate: Instant,
    val updatePeriod: String?,
    val description: String?,
    val thumbnail: String?,
)
