package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class GetTimelineStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        minId: String?,
        limit: Int,
        listId: String? = null,
    ): Result<List<Status>> {
        val timelineRepo = clientManager.getClient(locator).timelinesRepo
        val platformResult = platformRepo.getPlatform(locator)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val entitiesResult = when (type) {
            ActivityPubStatusSourceType.TIMELINE_HOME -> timelineRepo.homeTimeline(
                limit = limit,
                minId = minId,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.TIMELINE_LOCAL -> timelineRepo.localTimelines(
                limit = limit,
                minId = minId,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.TIMELINE_PUBLIC -> timelineRepo.publicTimelines(
                limit = limit,
                minId = minId,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.LIST -> timelineRepo.getTimelineList(
                listId = listId!!,
                limit = limit,
                minId = minId,
                maxId = maxId,
            )
        }
        return entitiesResult.map { list ->
            list.filter { it.id != minId && it.id != maxId }
                .map { statusAdapter.toStatus(it, platform) }
        }
    }
}
