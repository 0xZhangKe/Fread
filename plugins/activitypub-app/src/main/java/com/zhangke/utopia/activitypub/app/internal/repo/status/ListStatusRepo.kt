package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import javax.inject.Inject

class ListStatusRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusDatabase: ActivityPubStatusDatabase,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) : StatusRepo(statusDatabase, formatDatetimeToDate) {

    override suspend fun loadStatusFromServer(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String?
    ): Result<List<ActivityPubStatusEntity>> {
        val timelinesRepo = clientManager.getClient(serverBaseUrl).timelinesRepo
        return timelinesRepo.getTimelineList(
            listId = listId!!,
            limit = limit,
            maxId = maxId,
        )
    }

    suspend fun getLocalStatus(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): List<ActivityPubStatusEntity> {
        return getLocalStatusInternal(
            serverBaseUrl = serverBaseUrl,
            type = ActivityPubStatusSourceType.LIST,
            limit = limit,
            listId = listId,
        )
    }

    suspend fun getRemoteStatus(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return getRemoteStatusInternal(
            serverBaseUrl = serverBaseUrl,
            type = ActivityPubStatusSourceType.LIST,
            limit = limit,
            listId = listId,
        )
    }

    suspend fun loadMore(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        maxId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return loadMoreInternal(
            serverBaseUrl = serverBaseUrl,
            type = ActivityPubStatusSourceType.LIST,
            maxId = maxId,
            limit = limit,
            listId = listId,
        )
    }
}
