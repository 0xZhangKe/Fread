package com.zhangke.fread.status.model

import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

const val ACTIVITY_PUB_PROTOCOL_ID = "ActivityPub"
const val RSS_PROTOCOL_ID = "RSS"

@Serializable
@Parcelize
data class StatusProviderProtocol(
    val id: String,
    val name: String,
) : PlatformParcelable, PlatformSerializable

val StatusProviderProtocol.isActivityPub: Boolean
    get() = id == ACTIVITY_PUB_PROTOCOL_ID

val StatusProviderProtocol.notActivityPub: Boolean
    get() = !isActivityPub

val StatusProviderProtocol.isRss: Boolean
    get() = id == RSS_PROTOCOL_ID

val StatusProviderProtocol.notRss: Boolean
    get() = !isRss
