package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetTimelineStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val getStatusSupportInteractive: GetStatusInteractionUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
) {

    suspend operator fun invoke(
        timelineUriInsights: TimelineSourceUriInsights,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>> {
        val baseUrl = timelineUriInsights.serverBaseUrl
        val timelineRepo = clientManager.getClient(baseUrl).timelinesRepo
        val platformResult = platformRepo.getPlatform(baseUrl)
        if (platformResult.isFailure) return Result.failure(platformResult.exceptionOrNull()!!)
        val platform = platformResult.getOrThrow()
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
                val supportActions = getStatusSupportInteractive(it)
                activityPubStatusAdapter.toStatus(it, platform, supportActions)
            }
        }
    }
}
