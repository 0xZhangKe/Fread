package com.zhangke.fread.bluesky.internal.model

import app.bsky.feed.FeedViewPost
import com.zhangke.fread.status.status.model.Status

data class BskyPagingFeeds(
    val cursor: String?,
    val feeds: List<Status>,
)
