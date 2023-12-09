package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceUriInsights
import com.zhangke.utopia.activitypub.app.internal.usecase.client.GetClientUseCase
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetTimelineStatusUseCase @Inject constructor(
    private val getClientUseCase: GetClientUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        timelineUriInsights: TimelineSourceUriInsights,
        limit: Int,
    ): Result<List<Status>> {
        val timelineRepo = getClientUseCase(timelineUriInsights.serverBaseUrl).timelinesRepo
        return when (timelineUriInsights.type) {
            TimelineSourceType.HOME -> timelineRepo.homeTimeline(limit = limit)
            TimelineSourceType.LOCAL -> timelineRepo.localTimelines(limit = limit)
            TimelineSourceType.PUBLIC -> timelineRepo.publicTimelines(limit = limit)
        }.map { it.map(activityPubStatusAdapter::adapt) }
    }
}
