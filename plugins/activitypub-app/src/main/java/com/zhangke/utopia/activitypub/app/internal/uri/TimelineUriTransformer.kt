package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceUriData
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class TimelineUriTransformer @Inject constructor() {

    companion object {

        private const val QUERY_SERVER_BASE_URL = "serverBaseUrl"
        private const val QUERY_TYPE = "type"
    }

    fun parse(uri: StatusProviderUri): TimelineSourceUriData? {
        if (!uri.isActivityPubUri) return null
        if (uri.path != ActivityPubUriPath.TIMELINE) return null
        val serverBaseUrl = uri.queries[QUERY_SERVER_BASE_URL]
            ?.takeIf { it.isNotEmpty() } ?: return null
        val type = uri.queries[QUERY_TYPE]?.let(TimelineSourceType::valurOfOrNull) ?: return null
        return TimelineSourceUriData(
            uri = uri,
            serverBaseUrl = serverBaseUrl,
            type = type,
        )
    }

    fun build(serverBaseUrl: String, type: TimelineSourceType): StatusProviderUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_SERVER_BASE_URL] = serverBaseUrl
        queries[QUERY_TYPE] = type.stringValue
        return createActivityPubUri(
            path = ActivityPubUriPath.TIMELINE,
            queries = queries,
        )
    }
}
