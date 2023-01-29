package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.StatusProvider
import com.zhangke.utopia.blogprovider.StatusProviderFactory

class ActivityPubProviderFactory: StatusProviderFactory {

    override fun createProvider(source: BlogSource): StatusProvider? {
//        if (source.protocol != ACTIVITY_PUB_PROTOCOL) return null
//        return createTimelineProvider(source.sourceServer)
        return null
    }

//    fun createTimelineProvider(domain: String): StatusProvider {
//        val client = obtainActivityPubClient(domain)
//        return ActivityPubTimelineProvider(client)
//    }
}