package com.zhangke.fread.bluesky.internal.model

data class BlueskyProfile(
    val did: String,
    val handle: String,
    val displayName: String?,
    val description: String?,
    val avatar: String?,
)
