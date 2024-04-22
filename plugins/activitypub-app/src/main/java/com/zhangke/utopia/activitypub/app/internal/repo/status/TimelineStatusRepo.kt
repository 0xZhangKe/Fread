package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.ResolveBaseUrlUseCase
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject

class TimelineStatusRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    platformRepo: ActivityPubPlatformRepo,
    statusDatabase: ActivityPubStatusDatabase,
    formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    resolveBaseUrl: ResolveBaseUrlUseCase,
) : StatusRepo(platformRepo, statusDatabase, formatDatetimeToDate, resolveBaseUrl) {

    override suspend fun loadStatusFromServer(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String?,
    ): Result<List<ActivityPubStatusEntity>> {
        val timelineRepo = clientManager.getClient(role).timelinesRepo
        return when (type) {
            ActivityPubStatusSourceType.TIMELINE_HOME -> timelineRepo.homeTimeline(
                limit = limit,
                sinceId = null,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.TIMELINE_LOCAL -> timelineRepo.localTimelines(
                limit = limit,
                sinceId = null,
                maxId = maxId,
            )

            ActivityPubStatusSourceType.TIMELINE_PUBLIC -> timelineRepo.publicTimelines(
                limit = limit,
                sinceId = null,
                maxId = maxId,
            )

            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    suspend fun getLocalStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): List<ActivityPubStatusEntity> {
        return getLocalStatusInternal(
            role = role,
            type = type,
            limit = limit,
            listId = null,
        )
    }

    suspend fun getRemoteStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return getRemoteStatusInternal(
            role = role,
            type = type,
            limit = limit,
            listId = null,
        )
    }

    suspend fun loadMore(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return loadMoreInternal(
            role = role,
            type = type,
            maxId = maxId,
            limit = limit,
            listId = null,
        )
    }
}
