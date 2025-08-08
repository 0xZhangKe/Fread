package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
data class Relationships(
    val blocking: Boolean,
    val blockedBy: Boolean,
    val following: Boolean,
    val followedBy: Boolean,
    val muting: Boolean,
    val requested: Boolean?,
    val requestedBy: Boolean?,
)
