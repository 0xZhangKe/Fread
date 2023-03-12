package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.utopia.activitypubapp.buildActivityPubSourceUri
import com.zhangke.utopia.activitypubapp.requireActivityPubUri
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.activitypubapp.utils.decodeFromBase64
import com.zhangke.utopia.activitypubapp.utils.encodeToBase64
import com.zhangke.utopia.status_provider.StatusSourceUri

// LocalTimeline: statussource://activitypub.com/{server_url_base64}/timeline?query=local
// PublicTimeline: statussource://activitypub.com/{server_url_base64}/timeline?query=public
internal fun buildTimelineSourceUri(
    host: String,
    type: TimelineSourceType
): StatusSourceUri {
    val path = buildTimelineSourcePath(host)
    return buildActivityPubSourceUri(path, type.stringValue)
}

private fun buildTimelineSourcePath(host: String): String {
    return "${host.encodeToBase64()}/timeline"
}

internal fun getServerAndType(uri: StatusSourceUri): Pair<ActivityPubUrl, TimelineSourceType>? {
    uri.requireActivityPubUri()
    if (uri.query.isEmpty()) return null
    val path = uri.path
    if (!path.endsWith("/timeline")) return null
    val urlBase64 = path.removePrefix("/").removeSuffix("/timeline")
    val urlString = urlBase64.decodeFromBase64()
    val activityPubUrl = ActivityPubUrl.create(urlString) ?: return null
    val type = TimelineSourceType.values()
        .firstOrNull { it.stringValue == uri.query } ?: return null
    return activityPubUrl to type
}