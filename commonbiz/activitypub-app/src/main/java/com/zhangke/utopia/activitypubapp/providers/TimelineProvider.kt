package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.protocol.isTimelineSourceUri
import com.zhangke.utopia.activitypubapp.protocol.parseTimeline
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.Status
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/12/9.
 */
@Filt
class TimelineProvider @Inject constructor(
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) : IStatusProvider {

    override fun applicable(sourceUri: StatusProviderUri): Boolean {
        return sourceUri.isTimelineSourceUri()
    }

    override suspend fun requestStatuses(
        sourceUri: StatusProviderUri,
    ): Result<List<Status>> {
        val (url, type) = sourceUri.parseTimeline() ?: return Result.failure(
            IllegalArgumentException("$sourceUri is not Timeline source uri!")
        )
        val client = obtainActivityPubClientUseCase(url.host)
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
            list.map { activityPubStatusAdapter.adapt(it, url.host) }
        }
    }
}
