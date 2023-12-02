package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubPlatformUri private constructor(
    val serverHost: String,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "/server"

        private const val QUERY_HOST = "url"

        fun create(host: String): ActivityPubPlatformUri {
            val queries = mapOf(QUERY_HOST to host)
            return ActivityPubPlatformUri(host, queries)
        }

        fun parse(uri: String): ActivityPubPlatformUri? {
            return parse(from(uri)!!)
        }

        fun parse(uri: StatusProviderUri): ActivityPubPlatformUri? {
            if (uri.path != PATH) return null
            val host = uri.queries[QUERY_HOST]
            if (host.isNullOrEmpty()) return null
            return create(host)
        }
    }
}
