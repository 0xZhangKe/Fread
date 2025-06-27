package com.zhangke.fread.activitypub.app.internal.utils

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.status.model.PlatformLocator

fun createPlatformLocator(content: ActivityPubContent): PlatformLocator {
    return PlatformLocator(baseUrl = content.baseUrl, accountUri = content.accountUri)
}
