package com.zhangke.fread.bluesky.internal.model

import kotlinx.serialization.Serializable

@Serializable
data class BlueskyProfile(
    val did: String,
    val handle: String,
    val displayName: String?,
    val description: String?,
    val avatar: String?,
){

    val prettyHandle: String = if (handle.startsWith('@')) handle else "@$handle"
}
