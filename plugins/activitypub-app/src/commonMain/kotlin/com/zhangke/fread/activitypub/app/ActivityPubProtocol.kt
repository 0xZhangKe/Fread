package com.zhangke.fread.activitypub.app

import com.zhangke.fread.status.model.ACTIVITY_PUB_PROTOCOL_ID
import com.zhangke.fread.status.model.StatusProviderProtocol
import org.jetbrains.compose.resources.getString

suspend fun createActivityPubProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = ACTIVITY_PUB_PROTOCOL_ID,
        name = getString(Res.string.activity_pub_protocol_name),
    )
}
