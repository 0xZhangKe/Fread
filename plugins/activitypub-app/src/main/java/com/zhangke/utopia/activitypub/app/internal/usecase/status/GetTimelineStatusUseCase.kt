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
    private val getStatusSupportAction: GetStatusSupportActionUseCase,
) {

    suspend operator fun invoke(
        timelineUriInsights: TimelineSourceUriInsights,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>> {
        val timelineRepo = getClientUseCase(timelineUriInsights.serverBaseUrl).timelinesRepo
        return when (timelineUriInsights.type) {
            TimelineSourceType.HOME -> timelineRepo.homeTimeline(
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )

            TimelineSourceType.LOCAL -> timelineRepo.localTimelines(
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )

            TimelineSourceType.PUBLIC -> timelineRepo.publicTimelines(
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }.map { list ->
            list.map {
                val supportActions = getStatusSupportAction(it)
                activityPubStatusAdapter.toStatus(it, supportActions)
            }
        }
    }
}
