package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.status.model.PlatformLocator

expect class ActivityPubPushManager {

    suspend fun subscribe(locator: PlatformLocator, accountId: String)

    suspend fun unsubscribe(locator: PlatformLocator, accountId: String)
}
