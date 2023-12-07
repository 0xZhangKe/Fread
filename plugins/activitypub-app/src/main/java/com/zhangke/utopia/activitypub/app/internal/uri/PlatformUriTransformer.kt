package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.activitypub.app.internal.model.PlatformUriData
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class PlatformUriTransformer @Inject constructor() {

    companion object {
        private const val QUERY_SERVER_BASE_URL = "serverBaseUrl"
    }

    fun build(serverBaseUrl: String): StatusProviderUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_SERVER_BASE_URL] = serverBaseUrl
        return createActivityPubUri(
            path = ActivityPubUriPath.PLATFORM,
            queries = queries,
        )
    }

    fun parse(uri: StatusProviderUri): PlatformUriData? {
        if (!uri.isActivityPubUri) return null
        if (uri.path != ActivityPubUriPath.PLATFORM) return null
        val serverBaseUrl = uri.queries[QUERY_SERVER_BASE_URL]
        if (serverBaseUrl.isNullOrEmpty()) return null
        return PlatformUriData(uri, serverBaseUrl)
    }
}
