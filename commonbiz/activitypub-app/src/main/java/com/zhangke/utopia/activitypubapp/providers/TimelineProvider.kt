package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.status_provider.IStatusProvider
import com.zhangke.utopia.status_provider.Status
import com.zhangke.utopia.status_provider.StatusSource

/**
 * Created by ZhangKe on 2022/12/9.
 */
internal class TimelineProvider(private val source: TimelineSource) : IStatusProvider {

    override fun applicable(source: StatusSource): Boolean {
        return source is TimelineSource
    }

    override suspend fun requestStatuses(source: StatusSource): Result<List<Status>> {
        source as TimelineSource
        val host = source.host
        val client = obtainActivityPubClient(host)
        val timelineRepo = client.timelinesRepo
        return when (source.type) {
            TimelineSourceType.PUBLIC -> timelineRepo.publicTimelines()
            TimelineSourceType.LOCAL -> timelineRepo.localTimelines()
            TimelineSourceType.HOME -> timelineRepo.homeTimeline(
                maxId = "",
                sinceId = "",
                minId = "",
                limit = 50
            )
        }.toStatus(host)
    }
}