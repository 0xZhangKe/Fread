package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.protocol.parseTimeline
import com.zhangke.utopia.activitypubapp.protocol.isTimelineSourceUri
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.Status
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/12/9.
 */
internal class TimelineProvider @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) : IStatusProvider {

    override fun applicable(sourceUri: StatusProviderUri): Boolean {
        return sourceUri.isTimelineSourceUri()
    }

    override suspend fun requestStatuses(
        sourceUri: StatusProviderUri,
    ): Result<List<Status>> {
        val type = sourceUri.parseTimeline()?.second ?: return Result.failure(
            IllegalArgumentException("$sourceUri is not Timeline source uri!")
        )
        val host = sourceUri.host
        val client = obtainActivityPubClient(host)
        val timelineRepo = client.timelinesRepo

        return when (type) {
            TimelineSourceType.PUBLIC -> timelineRepo.publicTimelines()
            TimelineSourceType.LOCAL -> timelineRepo.localTimelines()
            TimelineSourceType.HOME -> timelineRepo.homeTimeline(
                maxId = "",
                sinceId = "",
                minId = "",
                limit = 50
            )
        }.map { list ->
            list.map { activityPubStatusAdapter.adapt(it, host) }
        }
    }
}