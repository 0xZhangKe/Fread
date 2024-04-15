package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubPollEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.ResolveBaseUrlUseCase
import com.zhangke.utopia.status.model.IdentityRole

abstract class StatusRepo(
    private val statusDatabase: ActivityPubStatusDatabase,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val resolveBaseUrl: ResolveBaseUrlUseCase,
) {

    private val statusDao get() = statusDatabase.getDao()

    protected abstract suspend fun loadStatusFromServer(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String? = null,
    ): Result<List<ActivityPubStatusEntity>>

    protected suspend fun getLocalStatusInternal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String? = null,
    ): List<ActivityPubStatusEntity> {
        return queryListFromLocal(
            role = role,
            type = type,
            limit = limit,
            listId = listId,
        )
    }

    protected suspend fun getRemoteStatusInternal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        return loadStatusFromServer(
            role = role,
            type = type,
            maxId = null,
            limit = limit,
            listId = listId,
        ).onSuccess {
            replaceLocalStatus(
                role = role,
                type = type,
                listId = listId,
                statuses = it,
            )
        }
    }

    protected suspend fun loadMoreInternal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        limit: Int,
        listId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        val allStatusFromLocal = queryListFromLocal(
            role = role,
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
            role = role,
            type = type,
            maxId = maxId,
            limit = limit,
            listId = listId,
        ).onSuccess {
            appendToLocal(
                role = role,
                type = type,
                maxId = maxId,
                listId = listId,
                statuses = it,
            )
        }
    }

    private suspend fun queryListFromLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String?,
    ): List<ActivityPubStatusEntity> {
        val baseUrl = resolveBaseUrl(role)
        return if (!listId.isNullOrEmpty() && type == ActivityPubStatusSourceType.LIST) {
            statusDao
                .queryListStatus(baseUrl, ActivityPubStatusSourceType.LIST, listId, limit)
        } else {
            statusDao.query(baseUrl, type, limit)
        }.map { it.status }
    }

    private suspend fun appendToLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String?,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        if (statusDao.query(maxId) == null) return
        insertEntities(role, type, listId, statuses)
    }

    private suspend fun replaceLocalStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        if (listId.isNullOrEmpty().not() && type == ActivityPubStatusSourceType.LIST) {
            statusDao.deleteListStatus(resolveBaseUrl(role), type, listId!!)
        } else {
            statusDao.delete(resolveBaseUrl(role), type)
        }
        insertEntities(role, type, listId, statuses)
    }

    private suspend fun insertEntities(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<ActivityPubStatusEntity>,
    ) {
        val tableEntities = statuses.map {
            it.toTableEntity(
                role = role,
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

    suspend fun updatePoll(id: String, poll: ActivityPubPollEntity) {
        val originEntity = statusDao.query(id) ?: return
        val newEntity = originEntity.copy(
            status = originEntity.status.copy(poll = poll),
        )
        statusDao.insert(newEntity)
    }

    private fun ActivityPubStatusEntity.toTableEntity(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String? = null,
    ): ActivityPubStatusTableEntity {
        val baseUrl = resolveBaseUrl(role)
        return ActivityPubStatusTableEntity(
            id = this.id,
            type = type,
            serverBaseUrl = baseUrl,
            listId = listId,
            status = this,
            createTimestamp = formatDatetimeToDate(this.createdAt).time,
        )
    }
}
