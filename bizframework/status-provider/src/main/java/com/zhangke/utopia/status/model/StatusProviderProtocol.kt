package com.zhangke.utopia.status.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

const val ACTIVITY_PUB_PROTOCOL_ID = "ActivityPub"
const val RSS_PROTOCOL_ID = "RSS"

@Serializable
@Parcelize
data class StatusProviderProtocol(
    val id: String,
    val name: String,
) : Parcelable, java.io.Serializable

val StatusProviderProtocol.isActivityPub: Boolean
    get() = id == ACTIVITY_PUB_PROTOCOL_ID
