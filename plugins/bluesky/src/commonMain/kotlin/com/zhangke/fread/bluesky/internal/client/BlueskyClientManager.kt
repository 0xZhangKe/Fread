package com.zhangke.fread.bluesky.internal.client

import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class BlueskyClientManager @Inject constructor() {

    private val cachedClient = mutableMapOf<IdentityRole, BlueskyClient>()

    private val httpClientEngine by lazy {
        createHttpClientEngine()
    }

    fun clearCache() {
        cachedClient.clear()
    }

    fun getClient(role: IdentityRole): BlueskyClient {
        cachedClient[role]?.let { return it }
        return createClient(role.baseUrl!!).also { cachedClient[role] = it }
    }

    private fun createClient(baseUrl: FormalBaseUrl): BlueskyClient {
        return BlueskyClient(
            baseUrl = baseUrl,
            engine = httpClientEngine,
            json = globalJson,
        )
    }
}
