package com.zhangke.fread.rss

import com.zhangke.fread.status.model.RSS_PROTOCOL_ID
import com.zhangke.fread.status.model.StatusProviderProtocol
import org.jetbrains.compose.resources.getString

suspend fun createRssProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = RSS_PROTOCOL_ID,
        name = getString(Res.string.rss_protocol_name),
    )
}
