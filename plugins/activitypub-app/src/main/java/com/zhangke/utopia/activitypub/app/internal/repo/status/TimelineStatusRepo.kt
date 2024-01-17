package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import javax.inject.Inject

class TimelineStatusRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    statusDatabase: ActivityPubStatusDatabase,
    formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) : StatusRepo(statusDatabase, formatDatetimeToDate) {

    override suspend fun loadStatusFromServer(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String?
    ): Result<List<ActivityPubStatusEntity>> {
        val timelineRepo = clientManager.getClient(serverBaseUrl).timelinesRepo
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
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): List<ActivityPubStatusEntity> {
        return getLocalStatusInternal(
            serverBaseUrl = serverBaseUrl,
            type = type,
            limit = limit,
            listId = null,
        )
    }

    suspend fun getRemoteStatus(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return getRemoteStatusInternal(
            serverBaseUrl = serverBaseUrl,
            type = type,
            limit = limit,
            listId = null,
        )
    }

    suspend fun loadMore(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        maxId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return loadMoreInternal(
            serverBaseUrl = serverBaseUrl,
            type = type,
            maxId = maxId,
            limit = limit,
            listId = null,
        )
    }
}
