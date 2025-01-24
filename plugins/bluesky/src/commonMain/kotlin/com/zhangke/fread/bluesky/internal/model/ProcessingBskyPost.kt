package com.zhangke.fread.bluesky.internal.model

import app.bsky.feed.FeedViewPostReasonUnion
import app.bsky.feed.PostView

/**
 * 从 FeedViewPost 转换为 Status 的中间产物。
 */
data class ProcessingBskyPost(
    val postView: PostView,
    val pinned: Boolean,
    val reason: FeedViewPostReasonUnion?,
)
