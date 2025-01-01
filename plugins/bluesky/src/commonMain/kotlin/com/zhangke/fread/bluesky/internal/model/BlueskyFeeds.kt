package com.zhangke.fread.bluesky.internal.model

data class BlueskyFeeds(
    val uri: String,
    val cid: String,
    val did: String,
    val displayName: String,
    val description: String?,
    val avatar: String?,
    val likeCount: Long?,
    val creator: BlueskyProfile,
)
