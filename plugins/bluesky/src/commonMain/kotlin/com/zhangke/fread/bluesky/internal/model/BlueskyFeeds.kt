package com.zhangke.fread.bluesky.internal.model

import com.zhangke.fread.status.author.BlogAuthor

data class BlueskyFeeds(
    val uri: String,
    val cid: String,
    val did: String,
    val displayName: String,
    val description: String?,
    val avatar: String?,
    val likeCount: Long?,
    val creator: BlogAuthor,
)
