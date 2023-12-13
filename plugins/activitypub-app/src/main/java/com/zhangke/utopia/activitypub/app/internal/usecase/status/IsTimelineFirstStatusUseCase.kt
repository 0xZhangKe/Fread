package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceUriInsights
import com.zhangke.utopia.activitypub.app.internal.usecase.client.GetClientUseCase
import javax.inject.Inject

class IsTimelineFirstStatusUseCase @Inject constructor(
    private val getClient: GetClientUseCase,
) {

    suspend operator fun invoke(
        sourceUriInsights: TimelineSourceUriInsights,
        statusId: String,
    ): Result<Boolean> {
        val timelineRepo = getClient(sourceUriInsights.serverBaseUrl)
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
