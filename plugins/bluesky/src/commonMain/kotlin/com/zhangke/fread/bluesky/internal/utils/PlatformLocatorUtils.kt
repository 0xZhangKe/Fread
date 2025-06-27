package com.zhangke.fread.bluesky.internal.utils

import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.status.model.PlatformLocator

fun createPlatformLocator(content: BlueskyContent): PlatformLocator {
    return if (content.accountUri != null) {
        PlatformLocator(baseUrl = content.baseUrl, accountUri = content.accountUri)
    } else {
        PlatformLocator(baseUrl = content.baseUrl)
    }
}
