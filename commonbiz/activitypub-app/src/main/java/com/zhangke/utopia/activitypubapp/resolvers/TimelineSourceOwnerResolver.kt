package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.ActivityPubMaintainer
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supported url
 */
@Singleton
internal class TimelineSourceOwnerResolver @Inject constructor() :
    IActivityPubSourceMaintainerResolver {

    override suspend fun resolve(query: String): Result<StatusSourceOwner>? {
        val webFinger = WebFinger.create(query)
        // do not handle UserSource
        if (webFinger != null) return null
        val url = ActivityPubUrl.create(query) ?: return null
        return resolveByHost(url.host)
    }

    private suspend fun resolveByHost(host: String): Result<StatusSourceOwner> {
        val client = obtainActivityPubClient(host)
        val sourceList = listOf(
            TimelineSource(host, TimelineSourceType.HOME),
            TimelineSource(host, TimelineSourceType.LOCAL),
            TimelineSource(host, TimelineSourceType.PUBLIC),
        )
        return client.instanceRepo
            .getInstanceInformation()
            .map {
                ActivityPubMaintainer.fromActivityPubInstance(
                    instance = it,
                    sourceList = sourceList
                )
            }
    }
}
