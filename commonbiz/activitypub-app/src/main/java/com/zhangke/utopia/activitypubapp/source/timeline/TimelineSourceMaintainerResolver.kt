package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.ActivityPubMaintainer
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status_provider.StatusSourceMaintainer
import com.zhangke.utopia.status_provider.ISourceMaintainerResolver

/**
 * Supported url
 */
object TimelineSourceMaintainerResolver : ISourceMaintainerResolver {

    override suspend fun resolve(content: String): StatusSourceMaintainer? {
        val url = ActivityPubUrl.create(content) ?: return null
        return resolveByHost(url.host)
    }

    internal suspend fun resolveByHost(host: String): StatusSourceMaintainer {
        val client = obtainActivityPubClient(host)
        val instance = client.instanceRepo.getInstanceInformation().getOrThrow()
        val sourceList = listOf(
            TimelineSource(host, TimelineSourceType.HOME),
            TimelineSource(host, TimelineSourceType.LOCAL),
            TimelineSource(host, TimelineSourceType.PUBLIC),
        )
        return ActivityPubMaintainer.fromActivityPubInstance(
            instance = instance,
            sourceList = sourceList
        )
    }
}