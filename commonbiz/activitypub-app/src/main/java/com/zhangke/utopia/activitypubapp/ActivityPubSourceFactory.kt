package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.utils.RegexFactory
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.BlogSourceFactory

class ActivityPubSourceFactory : BlogSourceFactory {

    override suspend fun tryCreateSource(uri: String): BlogSource? {
        val domain = RegexFactory.getDomainRegex().find(uri)?.groups?.first()?.value
        if (domain.isNullOrEmpty()) return null
        return newActivityPubClient(domain)
            .instanceRepo
            .getInstanceInformation()
            .getOrNull()
            ?.toSource()
    }

    override suspend fun validate(source: BlogSource): Boolean {
        if (source.protocol != ACTIVITY_PUB_PROTOCOL) return false
        val timeline = newActivityPubClient(source.sourceServer)
            .timelinesRepo
            .localTimelines()
            .getOrNull()
        return timeline != null
    }

    private fun ActivityPubInstance.toSource(): BlogSource {
        return BlogSource(
            sourceServer = domain,
            sourceDescription = description,
            sourceName = title,
            avatar = thumbnail.url,
            protocol = ACTIVITY_PUB_PROTOCOL
        )
    }
}