package com.zhangke.fread.bluesky.internal.uri.platform

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.uri.BlueskyUriPath
import com.zhangke.fread.bluesky.internal.uri.createBlueskyUri
import com.zhangke.fread.bluesky.internal.uri.isBlueskyUri
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class PlatformUriTransformer @Inject constructor() {

    companion object {
        private const val QUERY_SERVER_BASE_URL = "serverBaseUrl"
    }

    fun build(serverBaseUrl: FormalBaseUrl): FormalUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_SERVER_BASE_URL] = serverBaseUrl.toString()
        return createBlueskyUri(
            path = BlueskyUriPath.PLATFORM,
            queries = queries,
        )
    }

    fun parse(uri: FormalUri): PlatformUriInsights? {
        if (!uri.isBlueskyUri) return null
        if (uri.path != BlueskyUriPath.PLATFORM) return null
        val serverBaseUrl = uri.queries[QUERY_SERVER_BASE_URL]
        if (serverBaseUrl.isNullOrEmpty()) return null
        return PlatformUriInsights(
            uri,
            FormalBaseUrl.parse(serverBaseUrl)!!
        )
    }
}
