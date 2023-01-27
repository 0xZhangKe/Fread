package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.source.TimelineSourceResolver
import com.zhangke.utopia.activitypubapp.source.UserSourceResolver
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.activitypubapp.utils.toMetaSource
import com.zhangke.utopia.blogprovider.BlogSourceGroup
import com.zhangke.utopia.blogprovider.BlogSourceResolver

class ActivityPubSourceResolver : BlogSourceResolver {

    private val resolverList = listOf(
        UserSourceResolver(),
        TimelineSourceResolver(false),
        TimelineSourceResolver(true)
    )

    private val instanceCache = mutableMapOf<ActivityPubApplicableUrl, ActivityPubInstance>()

    override suspend fun resolve(content: String): BlogSourceGroup? {
        val url = ActivityPubApplicableUrl(content)
        if (!url.validate()) return null
        val instance = requestActivityPubInstance(url) ?: return null
        val sourceList = resolverList.mapNotNull { it.resolve(url, instance) }
        if (sourceList.isEmpty()) return null
        return BlogSourceGroup(
            metaSourceInfo = instance.toMetaSource(),
            sourceList = sourceList
        )
    }

    private suspend fun requestActivityPubInstance(url: ActivityPubApplicableUrl): ActivityPubInstance? {
        return instanceCache[url] ?: obtainActivityPubClient(url.host!!).instanceRepo
            .getInstanceInformation()
            .getOrNull()
            ?.also { instanceCache[url] = it }
    }
}