package com.zhangke.utopia.activitypubapp

import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceResolver
import com.zhangke.utopia.activitypubapp.source.user.UserSourceResolver
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.IStatusSourceResolver
import com.zhangke.utopia.status_provider.StatusSourceUri

class ActivityPubSourceResolver : IStatusSourceResolver {

    private val resolverList = listOf(
        UserSourceResolver(),
        TimelineSourceResolver(),
    )

    override fun applicable(uri: StatusSourceUri): Boolean {
        return uri.isActivityPubUri()
    }

    override suspend fun resolve(uri: StatusSourceUri): StatusSource? {
        uri.requireActivityPubUri()
        return resolverList.firstOrNull { it.applicable(uri) }?.resolve(uri)
    }
}