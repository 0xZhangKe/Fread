package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.status.model.PlatformLocator

actual class ActivityPubPushManager() {

    actual suspend fun subscribe(locator: PlatformLocator, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }

    actual suspend fun unsubscribe(locator: PlatformLocator, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }
}
