package com.zhangke.utopia.activitypub.app

import android.content.Context
import com.zhangke.utopia.status.model.ACTIVITY_PUB_PROTOCOL_ID
import com.zhangke.utopia.status.model.StatusProviderProtocol

fun createActivityPubProtocol(context: Context): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = ACTIVITY_PUB_PROTOCOL_ID,
        name = context.getString(R.string.activity_pub_protocol_name),
    )
}
