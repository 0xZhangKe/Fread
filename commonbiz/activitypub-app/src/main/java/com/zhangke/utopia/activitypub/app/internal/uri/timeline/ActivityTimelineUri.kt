package com.zhangke.utopia.activitypub.app.internal.uri.timeline

import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUri
import com.zhangke.utopia.activitypub.app.internal.uri.activityPubUriString
import com.zhangke.utopia.activitypub.app.internal.uri.user.ActivityPubUserUri
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityTimelineUri private constructor(
    val timelineServerHost: String,
    val type: TimelineSourceType,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "/timeline"

        internal const val QUERY_HOST = "host"
        internal const val QUERY_TYPE = "type"

        const val baseUrl: String = "${StatusProviderUri.SCHEME}://$HOST$PATH"

        fun create(host: String, type: TimelineSourceType): ActivityTimelineUri {
            val queries = mapOf(QUERY_HOST to host, QUERY_TYPE to type.stringValue)
            return ActivityTimelineUri(host, type, queries)
        }
    }
}
