package com.zhangke.fread.bluesky

import com.zhangke.fread.status.model.BLUESKY_PROTOCOL_ID
import com.zhangke.fread.status.model.StatusProviderProtocol
import org.jetbrains.compose.resources.getString

suspend fun createBlueskyProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = BLUESKY_PROTOCOL_ID,
        name = getString(Res.string.bluesky_protocol_name),
    )
}
