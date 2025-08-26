package com.zhangke.fread.status.model

import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

const val ACTIVITY_PUB_PROTOCOL_ID = "ActivityPub"
const val RSS_PROTOCOL_ID = "RSS"
const val BLUESKY_PROTOCOL_ID = "Bluesky"

@Serializable
@Parcelize
data class StatusProviderProtocol(
    val id: String,
    val name: String,
) : PlatformParcelable, PlatformSerializable

fun createActivityPubProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = ACTIVITY_PUB_PROTOCOL_ID,
        name = "Mastodon",
    )
}

fun createBlueskyProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = BLUESKY_PROTOCOL_ID,
        name = "Bluesky",
    )
}

fun createRssProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = RSS_PROTOCOL_ID,
        name = "RSS",
    )
}

val StatusProviderProtocol.isActivityPub: Boolean
    get() = id == ACTIVITY_PUB_PROTOCOL_ID

val StatusProviderProtocol.notActivityPub: Boolean
    get() = !isActivityPub

val StatusProviderProtocol.isRss: Boolean
    get() = id == RSS_PROTOCOL_ID

val StatusProviderProtocol.notRss: Boolean
    get() = !isRss

val StatusProviderProtocol.isBluesky: Boolean
    get() = id == BLUESKY_PROTOCOL_ID

val StatusProviderProtocol.notBluesky: Boolean
    get() = !isBluesky
