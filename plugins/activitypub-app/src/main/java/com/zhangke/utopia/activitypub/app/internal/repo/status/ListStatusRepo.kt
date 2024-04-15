package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.ResolveBaseUrlUseCase
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject

class ListStatusRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    statusDatabase: ActivityPubStatusDatabase,
    formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    resolveBaseUrl: ResolveBaseUrlUseCase,
) : StatusRepo(statusDatabase, formatDatetimeToDate, resolveBaseUrl) {

    override suspend fun loadStatusFromServer(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String?
    ): Result<List<ActivityPubStatusEntity>> {
        val timelinesRepo = clientManager.getClient(role).timelinesRepo
        return timelinesRepo.getTimelineList(
            listId = listId!!,
            limit = limit,
            maxId = maxId,
        )
    }

    suspend fun getLocalStatus(
        role: IdentityRole,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): List<ActivityPubStatusEntity> {
        return getLocalStatusInternal(
            role = role,
            type = ActivityPubStatusSourceType.LIST,
            limit = limit,
            listId = listId,
        )
    }

    suspend fun getRemoteStatus(
        role: IdentityRole,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return getRemoteStatusInternal(
            role = role,
            type = ActivityPubStatusSourceType.LIST,
            limit = limit,
            listId = listId,
        )
    }

    suspend fun loadMore(
        role: IdentityRole,
        listId: String,
        maxId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        return loadMoreInternal(
            role = role,
            type = ActivityPubStatusSourceType.LIST,
            maxId = maxId,
            limit = limit,
            listId = listId,
        )
    }
}
