package com.zhangke.utopia.activitypubapp.oauth

import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.TimelineSource
import com.zhangke.utopia.activitypubapp.source.UserSource
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusProviderAuthorizer

class ActivityPubAuthorizer : StatusProviderAuthorizer {

    override fun applicable(source: StatusSource): Boolean {
        return source is TimelineSource || source is UserSource
    }

    override suspend fun checkAuthorizer(source: StatusSource): Boolean {
        return when (source) {
            is TimelineSource -> {
                checkTimelineAuthorizer(source)
            }
            is UserSource -> {
                false
            }
            else -> false
        }
    }

    private suspend fun checkTimelineAuthorizer(source: TimelineSource): Boolean{
        val url = ActivityPubUrl(source.uri)
        val timeline = obtainActivityPubClient(url.toughHost).timelinesRepo.run {
            if (source.isLocal) {
                localTimelines()
            } else {
                publicTimelines()
            }
        }.getOrNull()
        return timeline != null
    }

    override fun perform() {

    }
}