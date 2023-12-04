package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubPlatformUri private constructor(
    val baseUrl: String,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "/server"

        private const val QUERY_BASE_URL = "url"

        fun create(baseUrl: String): ActivityPubPlatformUri {
            val queries = mapOf(QUERY_BASE_URL to baseUrl)
            return ActivityPubPlatformUri(baseUrl, queries)
        }

        fun parse(uri: String): ActivityPubPlatformUri? {
            return parse(from(uri)!!)
        }

        fun parse(uri: StatusProviderUri): ActivityPubPlatformUri? {
            if (uri.path != PATH) return null
            val baseUrl = uri.queries[QUERY_BASE_URL]
            if (baseUrl.isNullOrEmpty()) return null
            return create(baseUrl)
        }
    }
}
