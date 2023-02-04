package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.source.TimelineSource
import com.zhangke.utopia.activitypubapp.source.UserSource
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.StatusProvider
import com.zhangke.utopia.blogprovider.StatusProviderFactory

class ActivityPubProviderFactory : StatusProviderFactory {

    override fun createProvider(source: BlogSource): StatusProvider? {
        if (source.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return when (source) {
            is TimelineSource -> TimelineProvider(source)
            is UserSource -> UserStatusProvider(source)
            else -> throw IllegalArgumentException("Unknown source type:${source}")
        }
    }
}