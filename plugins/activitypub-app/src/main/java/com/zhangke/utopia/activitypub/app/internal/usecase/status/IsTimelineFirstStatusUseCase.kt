package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceUriInsights
import javax.inject.Inject

class IsTimelineFirstStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        sourceUriInsights: TimelineSourceUriInsights,
        statusId: String,
    ): Result<Boolean> {
        val timelineRepo = clientManager.getClient(sourceUriInsights.serverBaseUrl)
            .timelinesRepo
        return when (sourceUriInsights.type) {
            TimelineSourceType.HOME -> timelineRepo.homeTimeline(
                limit = 1,
                maxId = statusId,
            )

            TimelineSourceType.LOCAL -> timelineRepo.localTimelines(
                limit = 1,
                maxId = statusId,
            )

            TimelineSourceType.PUBLIC -> timelineRepo.publicTimelines(
                limit = 1,
                maxId = statusId,
            )
        }.map { it.isEmpty() }
    }
}
