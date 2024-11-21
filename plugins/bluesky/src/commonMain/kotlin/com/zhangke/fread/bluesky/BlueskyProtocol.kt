package com.zhangke.fread.bluesky

import com.zhangke.fread.status.model.StatusProviderProtocol
import org.jetbrains.compose.resources.getString

private const val BLUESKY_PROTOCOL_ID = "bluesky"

suspend fun createBlueskyProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = BLUESKY_PROTOCOL_ID,
        name = getString(Res.string.bluesky_protocol_name),
    )
}
