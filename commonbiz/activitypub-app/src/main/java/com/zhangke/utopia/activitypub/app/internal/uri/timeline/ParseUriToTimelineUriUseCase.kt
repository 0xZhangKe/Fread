package com.zhangke.utopia.activitypub.app.internal.uri.timeline

import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSourceType
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ParseUriToTimelineUriUseCase @Inject constructor() {

    operator fun invoke(uri: StatusProviderUri): ActivityTimelineUri? {
        if (uri.path != ActivityTimelineUri.PATH) return null
        val queries = uri.queries
        val serverHost = queries[ActivityTimelineUri.QUERY_HOST]
        if (serverHost.isNullOrEmpty()) return null
        val type = queries[ActivityTimelineUri.QUERY_TYPE]
            ?.let { TimelineSourceType.valurOfOrNull(it) } ?: return null
        return ActivityTimelineUri.create(serverHost, type)
    }
}
