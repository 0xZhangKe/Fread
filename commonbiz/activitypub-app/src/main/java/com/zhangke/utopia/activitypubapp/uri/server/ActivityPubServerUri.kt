package com.zhangke.utopia.activitypubapp.uri.server

import com.zhangke.utopia.activitypubapp.uri.ActivityPubUri
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubServerUri private constructor(
    val host: String,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "/server"

        internal const val QUERY_HOST = "url"

        const val baseUrl: String = "${StatusProviderUri.SCHEME}://$HOST${PATH}"

        fun create(host: String): ActivityPubServerUri {
            val queries = mapOf(QUERY_HOST to host)
            return ActivityPubServerUri(host, queries)
        }
    }
}
