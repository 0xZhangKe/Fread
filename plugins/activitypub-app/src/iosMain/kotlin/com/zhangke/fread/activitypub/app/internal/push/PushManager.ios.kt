package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.status.model.IdentityRole

actual class PushManager {

    actual suspend fun subscribe(role: IdentityRole, accountId: String) {
        throw NotImplementedError("Not implemented for iOS")
    }
}
