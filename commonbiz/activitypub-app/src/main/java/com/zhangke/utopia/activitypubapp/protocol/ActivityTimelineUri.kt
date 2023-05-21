package com.zhangke.utopia.activitypubapp.protocol

import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.activitypubapp.utils.decodeFromBase64
import com.zhangke.utopia.activitypubapp.utils.encodeToBase64
import com.zhangke.utopia.status.source.StatusProviderUri

// /timeline/?host={server_url_base64}&type=local|public|home

private const val timelinePath = "/timeline"

internal fun buildTimelineSourceUri(
    host: String,
    type: TimelineSourceType
): StatusProviderUri {
    val queries = mapOf(
        "host" to host.encodeToBase64(),
        "type" to type.stringValue,
    )
    return buildActivityPubSourceUri(timelinePath, queries)
}

internal fun StatusProviderUri.isTimelineSourceUri(): Boolean {
    return isActivityPubUri() && path == timelinePath
}

internal fun StatusProviderUri.parseTimeline(): Pair<ActivityPubUrl, TimelineSourceType>? {
    if (isActivityPubUri().not()) return null
    if (path != timelinePath) return null
    val activityPubUrl = queries["host"]?.decodeFromBase64()?.let(ActivityPubUrl::create)
    val type = queries["type"]?.let(TimelineSourceType::valurOfOrNull)
    if (activityPubUrl == null || type == null) return null
    return activityPubUrl to type
}
