package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.utopia.activitypubapp.newActivityPubClient
import com.zhangke.utopia.blogprovider.StatusProvider

object ActivityPubProviderFactory {

    fun createTimelineProvider(domain: String): StatusProvider {
        val client = newActivityPubClient(domain)
        return ActivityPubTimelineProvider(client)
    }

    fun createTimelineProvider(app: ActivityPubApplication): StatusProvider {
        val client = newActivityPubClient(app)
        return ActivityPubTimelineProvider(client)
    }
}