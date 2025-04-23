package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class ActivityPubPushManager @Inject constructor() {

    actual suspend fun subscribe(role: IdentityRole, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }

    actual suspend fun unsubscribe(role: IdentityRole, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }
}
