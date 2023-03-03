package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.source.TimelineSource
import com.zhangke.utopia.activitypubapp.source.UserSource
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusProvider
import com.zhangke.utopia.status_provider.StatusProviderFactory

class ActivityPubProviderFactory : StatusProviderFactory {

    override fun createProvider(source: StatusSource): StatusProvider? {
        if (source.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return when (source) {
            is TimelineSource -> TimelineProvider(source)
            is UserSource -> UserStatusProvider(source)
            else -> throw IllegalArgumentException("Unknown source type:${source}")
        }
    }
}