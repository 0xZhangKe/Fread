package com.zhangke.utopia.activitypubapp.uri.server

import com.zhangke.utopia.activitypubapp.uri.ActivityPubUri
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubServerUri private constructor(
    val serverUrl: String,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "server"

        internal const val QUERY_URL = "url"

        const val BASE_URL = "${StatusProviderUri.SCHEME}://$HOST"

        fun create(serverUrl: String): ActivityPubServerUri {
            val queries = mapOf(QUERY_URL to serverUrl)
            return ActivityPubServerUri(serverUrl, queries)
        }
    }
}
