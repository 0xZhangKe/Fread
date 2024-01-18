package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase

abstract class StatusRepo(
    private val statusDatabase: ActivityPubStatusDatabase,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) {

    private val statusDao get() = statusDatabase.getDao()

    protected abstract suspend fun loadStatusFromServer(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String? = null,
    ): Result<List<ActivityPubStatusEntity>>

    protected suspend fun getLocalStatusInternal(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String? = null,
    ): List<ActivityPubStatusEntity> {
        return queryListFromLocal(
            serverBaseUrl = serverBaseUrl,
            type = type,
            limit = limit,
            listId = listId,
        )
    }

    protected suspend fun getRemoteStatusInternal(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        return loadStatusFromServer(
            serverBaseUrl = serverBaseUrl,
            type = type,
            maxId = null,
            limit = limit,
            listId = listId,
        ).onSuccess {
            replaceLocalStatus(
                serverBaseUrl = serverBaseUrl,
                type = type,
                listId = listId,
                statuses = it,
            )
        }
    }

    protected suspend fun loadMoreInternal(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        maxId: String,
        limit: Int,
        listId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        val allStatusFromLocal = queryListFromLocal(
            serverBaseUrl = serverBaseUrl,
            type = type,
            limit = Int.MAX_VALUE,
            listId = listId,
        )
        val index = allStatusFromLocal.indexOfFirst { it.id == maxId }
        if (index >= 0 && index < allStatusFromLocal.lastIndex) {
            return allStatusFromLocal.subList(index + 1, allStatusFromLocal.size)
                .take(limit)
                .let { Result.success(it) }
        }
        return loadStatusFromServer(
            serverBaseUrl = serverBaseUrl,
            type = type,
            maxId = maxId,
            limit = limit,
            listId = listId,
        ).onSuccess {
            appendToLocal(
                serverBaseUrl = serverBaseUrl,
                type = type,
                maxId = maxId,
                listId = listId,
                statuses = it,
            )
        }
    }

    private suspend fun queryListFromLocal(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String?,
    ): List<ActivityPubStatusEntity> {
        return if (!listId.isNullOrEmpty() && type == ActivityPubStatusSourceType.LIST) {
            statusDao
                .queryListStatus(serverBaseUrl, ActivityPubStatusSourceType.LIST, listId, limit)
        } else {
            statusDao.query(serverBaseUrl, type, limit)
        }.map { it.status }
    }

    private suspend fun appendToLocal(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String?,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        if (statusDao.query(maxId) == null) return
        insertEntities(serverBaseUrl, type, listId, statuses)
    }

    private suspend fun replaceLocalStatus(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        if (listId.isNullOrEmpty().not() && type == ActivityPubStatusSourceType.LIST) {
            statusDao.deleteListStatus(serverBaseUrl, type, listId!!)
        } else {
            statusDao.delete(serverBaseUrl, type)
        }
        insertEntities(serverBaseUrl, type, listId, statuses)
    }

    private suspend fun insertEntities(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        val tableEntities = statuses.map {
            it.toTableEntity(
                serverBaseUrl = serverBaseUrl,
                type = type,
                listId = listId,
            )
        }
        statusDao.insert(tableEntities)
    }

    suspend fun updateEntity(entity: ActivityPubStatusEntity) {
        val originEntity = statusDao.query(entity.id) ?: return
        val newEntity = originEntity.copy(
            status = entity,
            createTimestamp = formatDatetimeToDate(entity.createdAt).time,
        )
        statusDao.insert(newEntity)
    }

    private fun ActivityPubStatusEntity.toTableEntity(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        listId: String? = null,
    ): ActivityPubStatusTableEntity {
        return ActivityPubStatusTableEntity(
            id = this.id,
            type = type,
            serverBaseUrl = serverBaseUrl,
            listId = listId,
            status = this,
            createTimestamp = formatDatetimeToDate(this.createdAt).time,
        )
    }
}
