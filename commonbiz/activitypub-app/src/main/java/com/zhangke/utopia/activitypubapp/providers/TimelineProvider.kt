package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.ActivityPubSourceType
import com.zhangke.utopia.activitypubapp.source.TimelineSource
import com.zhangke.utopia.blogprovider.Status
import com.zhangke.utopia.blogprovider.StatusProvider

/**
 * Created by ZhangKe on 2022/12/9.
 */
internal class TimelineProvider(private val source: TimelineSource) : StatusProvider {

    override suspend fun requestStatuses(): Result<List<Status>> {
        val host = source.domain
        val client = obtainActivityPubClient(host)
        val timelineRepo = client.timelinesRepo
        return when (source.type) {
            ActivityPubSourceType.PUBLIC_TIMELINE -> timelineRepo.publicTimelines()
            ActivityPubSourceType.LOCAL_TIMELINE -> timelineRepo.localTimelines()
            ActivityPubSourceType.HOME_TIMELINE -> timelineRepo.homeTimeline(
                maxId = "",
                sinceId = "",
                minId = "",
                limit = 50
            )
            else -> throw IllegalArgumentException("Unknown source type:${source.type}")
        }.toStatus(host)
    }
}