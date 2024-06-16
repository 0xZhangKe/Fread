package com.zhangke.fread.rss.internal.screen.source

import com.zhangke.fread.rss.internal.model.RssSource

data class RssSourceUiState(
    val source: RssSource? = null,
    val formattedAddDate: String?,
    val formattedLastUpdateDate: String?,
)
