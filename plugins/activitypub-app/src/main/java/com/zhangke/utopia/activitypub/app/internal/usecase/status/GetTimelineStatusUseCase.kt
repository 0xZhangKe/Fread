package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetTimelineStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        sinceId: String?,
        limit: Int,
        listId: String? = null,
    ): Result<List<Status>> {
        val timelineRepo = clientManager.getClient(role).timelinesRepo
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val entitiesResult = when (type) {
            ActivityPubStatusSourceType.TIMELINE_HOME -> timelineRepo.homeTimeline(
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.TIMELINE_LOCAL -> timelineRepo.localTimelines(
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.TIMELINE_PUBLIC -> timelineRepo.publicTimelines(
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.LIST -> timelineRepo.getTimelineList(
                listId = listId!!,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }
        return entitiesResult.map { list ->
            list.map { statusAdapter.toStatus(it, platform) }
        }
    }
}
