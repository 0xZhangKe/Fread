package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.status.model.IdentityRole

expect class PushManager {

    suspend fun subscribe(role: IdentityRole, accountId: String)

    suspend fun unsubscribe(role: IdentityRole, accountId: String)
}
