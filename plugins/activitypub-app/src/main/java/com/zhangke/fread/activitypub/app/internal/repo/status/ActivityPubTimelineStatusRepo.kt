package com.zhangke.fread.activitypub.app.internal.repo.status

import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import javax.inject.Inject

class ActivityPubTimelineStatusRepo @Inject constructor(
    activityPubStatusDatabases: ActivityPubStatusDatabases,
    private val getTimeline: GetTimelineStatusUseCase,
) {

    private val statusDao = activityPubStatusDatabases.getDao()
    private val statusConfig = StatusConfigurationDefault.config

    suspend fun getFresherStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String? = null,
        limit: Int = statusConfig.loadFromServerLimit,
    ): Result<List<Status>> {
        return getTimeline(
            role = role,
            type = type,
            limit = limit,
            listId = listId,
            maxId = null,
            sinceId = null,
        ).onSuccess {
            saveFresherStatus(
                role = role,
                type = type,
                listId = listId,
                statusList = it,
            )
        }
    }

    private suspend fun saveFresherStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statusList: List<Status>,
    ) {
        if (statusList.isEmpty()) return
        val earlierStatus = statusList.minBy { it.datetime }
        val localEarlierStatus = queryEarlierStatus(
            role = role,
            type = type,
            listId = listId,
            datetime = earlierStatus.datetime,
            limit = 1,
        )
        if (localEarlierStatus.isEmpty()) {
            // local data are expired
            deleteStatus(role, type, listId)
        }
        statusList.insertToLocal(role, type, listId)
    }

    suspend fun loadPreviousPageStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        sinceId: String,
        listId: String? = null,
        limit: Int = statusConfig.loadFromServerLimit,
    ): Result<List<Status>> {
        return getTimeline(
            role = role,
            type = type,
            limit = limit,
            maxId = null,
            sinceId = sinceId,
            listId = listId,
        ).onSuccess {
            it.insertToLocal(role, type, listId)
        }
    }

    suspend fun getStatusFromLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = statusConfig.loadFromLocalLimit,
        listId: String? = null,
    ): List<Status> {
        return queryLocalStatusList(
            role = role,
            type = type,
            limit = limit,
            listId = listId,
        ).map { it.status }
    }

    suspend fun loadMore(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String? = null,
        limit: Int = statusConfig.loadFromLocalLimit,
    ): Result<List<Status>> {
        val localStatusList = loadMoreFromLocal(
            role = role,
            type = type,
            maxId = maxId,
            listId = listId,
        )
        if (localStatusList.isNotEmpty()) return Result.success(localStatusList)
        return getTimeline(
            role = role,
            type = type,
            limit = limit,
            maxId = maxId,
            sinceId = null,
            listId = listId,
        ).onSuccess {
            it.insertToLocal(role, type, listId)
        }
    }

    suspend fun updateStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        status: Status,
        listId: String? = null,
    ) {
        statusDao.insert(status.toDBEntity(role, type, listId))
    }

    suspend fun deleteStatus(statusId: String) {
        statusDao.delete(statusId)
    }

    private suspend fun loadMoreFromLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String? = null,
    ): List<Status> {
        val localStatus = queryLocalStatus(
            role = role,
            type = type,
            listId = listId,
            statusId = maxId,
        ) ?: return emptyList()
        return queryEarlierStatus(
            role = role,
            type = type,
            listId = listId,
            datetime = localStatus.createTimestamp,
            limit = StatusConfigurationDefault.config.loadFromLocalLimit,
        ).filter { it.id != localStatus.id }
    }

    private suspend fun List<Status>.insertToLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ) {
        statusDao.insert(this.toDBEntities(role, type, listId))
    }

    private suspend fun queryLocalStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statusId: String,
    ): ActivityPubStatusTableEntity? {
        return if (type == ActivityPubStatusSourceType.LIST) {
            statusDao.queryStatusInList(
                role = role,
                type = type,
                listId = listId!!,
                id = statusId,
            )
        } else {
            statusDao.query(role, type, statusId)
        }
    }

    private suspend fun queryLocalStatusList(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String?,
    ): List<ActivityPubStatusTableEntity> {
        return if (type == ActivityPubStatusSourceType.LIST) {
            statusDao.queryListStatus(
                role = role,
                type = type,
                limit = limit,
                listId = listId!!,
            )
        } else {
            statusDao.queryTimelineStatus(role, type, limit)
        }
    }

    private suspend fun queryEarlierStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        datetime: Long,
        limit: Int,
    ): List<Status> {
        return if (listId.isNullOrEmpty()) {
            statusDao.queryEarlierStatus(
                role = role,
                type = type,
                limit = limit,
                datetime = datetime,
            ).map { it.status }
        } else {
            statusDao.queryEarlierListStatus(
                role = role,
                type = type,
                limit = limit,
                listId = listId,
                datetime = datetime,
            ).map { it.status }
        }
    }

    private suspend fun deleteStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ) {
        if (listId.isNullOrEmpty()) {
            statusDao.delete(role, type)
        } else {
            statusDao.deleteListStatus(role, type, listId)
        }
    }

    private fun List<Status>.toDBEntities(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ): List<ActivityPubStatusTableEntity> {
        return this.map {
            it.toDBEntity(
                role = role,
                type = type,
                listId = listId,
            )
        }
    }

    private fun Status.toDBEntity(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ): ActivityPubStatusTableEntity {
        return ActivityPubStatusTableEntity(
            id = this.id,
            role = role,
            type = type,
            createTimestamp = this.datetime,
            listId = listId.orEmpty(),
            status = this,
        )
    }
}
