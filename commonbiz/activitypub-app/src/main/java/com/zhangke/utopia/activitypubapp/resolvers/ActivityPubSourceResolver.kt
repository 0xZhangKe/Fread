package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.isActivityPubUri
import com.zhangke.utopia.activitypubapp.requireActivityPubUri
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject

class ActivityPubSourceResolver @Inject constructor() : IStatusSourceResolver {

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