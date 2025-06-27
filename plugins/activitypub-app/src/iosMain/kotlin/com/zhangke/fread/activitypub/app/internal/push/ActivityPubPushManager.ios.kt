package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class ActivityPubPushManager @Inject constructor() {

    actual suspend fun subscribe(locator: PlatformLocator, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }

    actual suspend fun unsubscribe(locator: PlatformLocator, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }
}
