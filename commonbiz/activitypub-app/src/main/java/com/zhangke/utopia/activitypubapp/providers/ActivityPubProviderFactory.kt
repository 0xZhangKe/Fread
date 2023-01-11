package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.newActivityPubClient
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.StatusProvider
import com.zhangke.utopia.blogprovider.StatusProviderFactory

class ActivityPubProviderFactory: StatusProviderFactory {

    override fun createProvider(source: BlogSource): StatusProvider? {
        if (source.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return createTimelineProvider(source.sourceServer)
    }

    fun createTimelineProvider(domain: String): StatusProvider {
        val client = newActivityPubClient(domain)
        return ActivityPubTimelineProvider(client)
    }

    fun createTimelineProvider(app: ActivityPubApplication): StatusProvider {
        val client = newActivityPubClient(app)
        return ActivityPubTimelineProvider(client)
    }
}