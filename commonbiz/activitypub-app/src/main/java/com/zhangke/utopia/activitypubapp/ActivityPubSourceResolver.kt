package com.zhangke.utopia.activitypubapp

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.activitypubapp.source.TimelineSourceResolver
import com.zhangke.utopia.activitypubapp.source.UserSourceResolver
import com.zhangke.utopia.blogprovider.BlogSourceGroup
import com.zhangke.utopia.blogprovider.BlogSourceResolver
import java.util.*

class ActivityPubSourceResolver : BlogSourceResolver {

    private val resolverLinkedList = LinkedList<BlogSourceResolver>()

    init {
        // Must keep order.
        resolverLinkedList += UserSourceResolver()
        resolverLinkedList += TimelineSourceResolver()
    }

    override suspend fun resolve(content: String): BlogSourceGroup? {
        return resolverLinkedList.mapFirstOrNull { it.resolve(content) }
    }
}