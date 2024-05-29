package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class ListStatusRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    statusRepo: ExpiredActivityPubStatusRepo,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
) : StatusRepo(statusRepo) {

    override suspend fun loadStatusFromServer(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String?
    ): Result<List<Status>> {
        val timelinesRepo = clientManager.getClient(role).timelinesRepo
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return timelinesRepo.getTimelineList(
            listId = listId!!,
            limit = limit,
            maxId = maxId,
        ).map { list ->
            list.map { statusAdapter.toStatus(it, platform) }
        }
    }

    suspend fun getLocalStatus(
        role: IdentityRole,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): List<Status> {
        return getLocalStatusInternal(
            role = role,
            type = ActivityPubStatusSourceType.LIST,
            limit = limit,
            listId = listId,
        )
    }
}
