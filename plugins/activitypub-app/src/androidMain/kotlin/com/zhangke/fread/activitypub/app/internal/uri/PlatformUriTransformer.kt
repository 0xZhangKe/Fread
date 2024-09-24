package com.zhangke.fread.activitypub.app.internal.uri

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.model.PlatformUriInsights
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class PlatformUriTransformer @Inject constructor() {

    companion object {
        private const val QUERY_SERVER_BASE_URL = "serverBaseUrl"
    }

    fun build(serverBaseUrl: FormalBaseUrl): FormalUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_SERVER_BASE_URL] = serverBaseUrl.toString()
        return createActivityPubUri(
            path = ActivityPubUriPath.PLATFORM,
            queries = queries,
        )
    }

    fun parse(uri: FormalUri): PlatformUriInsights? {
        if (!uri.isActivityPubUri) return null
        if (uri.path != ActivityPubUriPath.PLATFORM) return null
        val serverBaseUrl = uri.queries[QUERY_SERVER_BASE_URL]
        if (serverBaseUrl.isNullOrEmpty()) return null
        return PlatformUriInsights(uri, FormalBaseUrl.parse(serverBaseUrl)!!)
    }
}
