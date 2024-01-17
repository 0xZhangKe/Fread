package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.adapter.ActivityPubStatusTableEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import javax.inject.Inject

class ListStatusRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusDatabase: ActivityPubStatusDatabase,
    private val activityPubStatusTableEntityAdapter: ActivityPubStatusTableEntityAdapter,
) {

    private val statusDao get() = statusDatabase.getDao()

    suspend fun getLocalStatus(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): List<ActivityPubStatusEntity> {
        return statusDao
            .queryListStatus(serverBaseUrl, ActivityPubStatusSourceType.LIST, listId, limit)
            .map { it.status }
    }

    suspend fun getRemoteStatus(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        val timelinesRepo = clientManager.getClient(serverBaseUrl).timelinesRepo
        return timelinesRepo.getTimelineList(
            listId = listId,
            limit = limit,
        ).onSuccess {
            replaceLocalStatus(serverBaseUrl, listId, it)
        }
    }

    suspend fun loadMore(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        maxId: String,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<ActivityPubStatusEntity>> {
        val allStatusFromLocal = statusDao.queryListStatus(
            serverBaseUrl = serverBaseUrl,
            type = ActivityPubStatusSourceType.LIST,
            listId = listId,
            limit = Int.MAX_VALUE,
        ).map { it.status }
        val index = allStatusFromLocal.indexOfFirst { it.id == maxId }
        if (index >= 0 && index < allStatusFromLocal.lastIndex) {
            return allStatusFromLocal.subList(index + 1, allStatusFromLocal.size)
                .take(limit)
                .let { Result.success(it) }
        }
        return clientManager.getClient(serverBaseUrl)
            .timelinesRepo
            .getTimelineList(
                listId = listId,
                maxId = maxId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
            ).onSuccess {
                appendToLocal(
                    statuses = it,
                    serverBaseUrl = serverBaseUrl,
                    listId = listId,
                    maxId = maxId,
                )
            }
    }

    private suspend fun appendToLocal(
        serverBaseUrl: FormalBaseUrl,
        maxId: String,
        listId: String,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        if (statusDao.query(maxId) == null) return
        insertEntities(serverBaseUrl, listId, statuses)
    }

    private suspend fun replaceLocalStatus(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        statusDao.deleteListStatus(serverBaseUrl, ActivityPubStatusSourceType.LIST, listId)
        insertEntities(serverBaseUrl, listId, statuses)
    }

    private suspend fun insertEntities(
        serverBaseUrl: FormalBaseUrl,
        listId: String,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        val tableEntities = statuses.map {
            activityPubStatusTableEntityAdapter.toTableEntity(
                entity = it,
                serverBaseUrl = serverBaseUrl,
                type = ActivityPubStatusSourceType.LIST,
                listId = listId,
            )
        }
        statusDao.insert(tableEntities)
    }
}
