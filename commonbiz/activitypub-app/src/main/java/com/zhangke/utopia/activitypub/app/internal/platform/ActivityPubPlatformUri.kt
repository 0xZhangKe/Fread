package com.zhangke.utopia.activitypub.app.internal.platform

import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUri
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubPlatformUri private constructor(
    val host: String,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "/server"

        internal const val QUERY_HOST = "url"

        const val baseUrl: String = "${StatusProviderUri.SCHEME}://$HOST$PATH"

        fun create(host: String): ActivityPubPlatformUri {
            val queries = mapOf(QUERY_HOST to host)
            return ActivityPubPlatformUri(host, queries)
        }

        fun parse(uri: String): ActivityPubPlatformUri? {
            return parse(StatusProviderUri.create(uri)!!)
        }

        fun parse(uri: StatusProviderUri): ActivityPubPlatformUri? {
            if (uri.path != PATH) return null
            val host = uri.queries[QUERY_HOST]
            if (host.isNullOrEmpty()) return null
            return create(host)
        }
    }
}
