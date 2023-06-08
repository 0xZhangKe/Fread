package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.utopia.activitypubapp.protocol.isActivityPubUri
import com.zhangke.utopia.activitypubapp.protocol.isTimelineSourceUri
import com.zhangke.utopia.activitypubapp.protocol.isUserSource
import com.zhangke.utopia.activitypubapp.protocol.parseTimeline
import com.zhangke.utopia.activitypubapp.protocol.parseUserInfo
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject

class FindHostFromUriUseCase @Inject constructor() {

    operator fun invoke(uri: String): String? {
        val activityPubUri = StatusProviderUri.create(uri) ?: return null
        if (!activityPubUri.isActivityPubUri()) return null
        if (activityPubUri.isUserSource()) {
            val host = activityPubUri.parseUserInfo()?.first?.host
            if (!host.isNullOrEmpty()) return host
        } else if (activityPubUri.isTimelineSourceUri()) {
            val host = activityPubUri.parseTimeline()?.first?.host
            if (!host.isNullOrEmpty()) return host
        }
        return null
    }
}
