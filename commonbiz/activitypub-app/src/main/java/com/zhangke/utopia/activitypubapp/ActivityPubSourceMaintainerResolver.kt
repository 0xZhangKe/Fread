package com.zhangke.utopia.activitypubapp

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceMaintainerResolver
import com.zhangke.utopia.activitypubapp.source.user.UserSourceMaintainerResolver
import com.zhangke.utopia.status_provider.StatusSourceMaintainer
import com.zhangke.utopia.status_provider.ISourceMaintainerResolver
import java.util.*

class ActivityPubSourceMaintainerResolver : ISourceMaintainerResolver {

    private val resolverLinkedList = LinkedList<ISourceMaintainerResolver>()

    init {
        // Must keep order.
        resolverLinkedList += UserSourceMaintainerResolver()
        resolverLinkedList += TimelineSourceMaintainerResolver
    }

    override suspend fun resolve(content: String): StatusSourceMaintainer? {
        return resolverLinkedList.mapFirstOrNull { it.resolve(content) }
    }
}