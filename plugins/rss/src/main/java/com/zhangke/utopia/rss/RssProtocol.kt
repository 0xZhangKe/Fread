package com.zhangke.utopia.rss

import android.content.Context
import com.zhangke.utopia.status.model.StatusProviderProtocol

internal const val RSS_PROTOCOL_ID = "RSS"

fun getRssProtocol(context: Context): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = RSS_PROTOCOL_ID,
        name = context.getString(R.string.rss_protocol_name),
    )
}
