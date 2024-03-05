package com.zhangke.utopia.activitypub.app

import android.content.Context
import com.zhangke.utopia.status.model.StatusProviderProtocol

internal const val ACTIVITY_PUB_PROTOCOL_ID = "ActivityPub"

fun getActivityPubProtocol(context: Context): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = ACTIVITY_PUB_PROTOCOL_ID,
        name = context.getString(R.string.activity_pub_protocol_name),
    )
}

internal val StatusProviderProtocol.isActivityPub: Boolean
    get() = id == ACTIVITY_PUB_PROTOCOL_ID
