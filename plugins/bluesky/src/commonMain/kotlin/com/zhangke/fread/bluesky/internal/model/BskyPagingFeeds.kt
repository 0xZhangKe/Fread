package com.zhangke.fread.bluesky.internal.model

import com.zhangke.fread.status.model.StatusUiState

data class BskyPagingFeeds(
    val cursor: String?,
    val feeds: List<StatusUiState>,
)
