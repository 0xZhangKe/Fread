package com.zhangke.fread.bluesky.internal.client

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class BlueskyClientManager @Inject constructor() {

    private val cachedClient = mutableMapOf<IdentityRole, BlueskyClient>()

    fun clearCache() {
        cachedClient.clear()
    }

    fun getClient(role: IdentityRole): BlueskyClient {
        return BlueskyClient()
    }
}
