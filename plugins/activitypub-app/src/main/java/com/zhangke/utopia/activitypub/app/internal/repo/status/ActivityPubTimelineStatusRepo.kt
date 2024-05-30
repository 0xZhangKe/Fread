package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class ActivityPubTimelineStatusRepo @Inject constructor(
    activityPubStatusDatabases: ActivityPubStatusDatabases,
    private val getTimeline: GetTimelineStatusUseCase,
) {

    private val statusDao = activityPubStatusDatabases.getDao()

    suspend fun getStatusFromServer(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
        sinceId: String? = null,
        listId: String? = null,
    ): Result<List<Status>> {
        return getStatusFromServerInternal(
            role = role,
            type = type,
            limit = limit,
            sinceId = sinceId,
            listId = listId,
        )
    }

    suspend fun getStatusFromLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
        listId: String? = null,
    ): List<ActivityPubStatusTableEntity> {
        return queryLocalStatusList(
            role = role,
            type = type,
            limit = limit,
            listId = listId,
        )
    }

    suspend fun loadMore(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String? = null,
    ): Result<List<ActivityPubStatusTableEntity>> {
        val localStatusList = loadMoreFromLocal(
            role = role,
            type = type,
            maxId = maxId,
            listId = listId,
        )
        if (localStatusList.isNotEmpty()) return Result.success(localStatusList)
        val serverStatus = getStatusFromServerInternal(
            role = role,
            type = type,
            maxId = maxId,
            listId = listId,
        )
        return emptyList()
    }

    private suspend fun getStatusFromServerInternal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
        sinceId: String? = null,
        maxId: String? = null,
        listId: String? = null,
    ): Result<List<ActivityPubStatusTableEntity>> {
        return getTimeline(
            role = role,
            type = type,
            limit = limit,
            maxId = maxId,
            sinceId = sinceId,
            listId = listId,
        ).onSuccess {
            saveStatusToLocal(
                role = role,
                type = type,
                statusList = it,
                listId = listId,
                maxId = maxId,
                sinceId = sinceId,
            )
        }
    }

    private suspend fun loadMoreFromLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String? = null,
    ): List<ActivityPubStatusTableEntity> {
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

    private suspend fun saveStatusToLocal(
        role: IdentityRole,
        statusList: List<Status>,
        type: ActivityPubStatusSourceType,
        listId: String?,
        /**
         * 获取到该页数据的 max id，如果有，则表示可能会将该条数据标识为非断裂的。
         */
        maxId: String? = null,
        /**
         * 获取到该页数据的 since id，如果有，则表示本地数据可能是连续的。
         */
        sinceId: String? = null,
    ) {
        if (statusList.isEmpty()) return
        if (maxId.isNullOrEmpty().not()) {
            // update max status not fracture
            queryLocalStatus(
                role = role,
                type = type,
                statusId = maxId!!,
                listId = listId
            )?.let {
                statusDao.insert(it.copy(fracture = false))
            }
        }
        val sortedStatus = statusList.sortedByDescending { it.datetime }
        val statusEntities = sortedStatus.mapIndexed { index, status ->
            val fracture = if (index == sortedStatus.lastIndex) {
                checkStatusIsFracture(
                    role = role,
                    type = type,
                    status = status,
                    sinceId = sinceId,
                    listId = listId,
                )
            } else {
                false
            }
            status.toDBEntity(
                role = role,
                type = type,
                fracture = fracture,
                listId = listId,
            )
        }
        statusDao.insert(statusEntities)
    }

    private suspend fun checkStatusIsFracture(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        status: Status,
        sinceId: String? = null,
        listId: String? = null,
    ): Boolean {
        val sinceStatus = sinceId?.let {
            queryLocalStatus(role = role, type = type, statusId = it, listId = listId)
        }
        if (sinceStatus != null) return false
        val allLocalStatus = queryLocalStatusList(
            role = role,
            type = type,
            limit = Int.MAX_VALUE,
            listId = listId,
        )
        val hasFresherStatus = allLocalStatus.any { it.status.datetime >= status.datetime }
        return !hasFresherStatus
    }

    private suspend fun queryLocalStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statusId: String,
    ): ActivityPubStatusTableEntity? {
        return if (type == ActivityPubStatusSourceType.LIST) {
            statusDao.query(role, type, statusId)
        } else {
            statusDao.queryStatusInList(
                role = role,
                type = type,
                listId = listId!!,
                id = statusId,
            )
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
    ): List<ActivityPubStatusTableEntity> {
        return if (listId.isNullOrEmpty()) {
            statusDao.queryEarlierStatus(
                role = role,
                type = type,
                limit = limit,
                datetime = datetime,
            )
        } else {
            statusDao.queryEarlierListStatus(
                role = role,
                type = type,
                limit = limit,
                listId = listId,
                datetime = datetime,
            )
        }
    }

    private fun List<Status>.toDBEntities(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        fracture: Boolean,
        listId: String?,
    ): List<ActivityPubStatusTableEntity> {
        return this.map {
            it.toDBEntity(
                role = role,
                type = type,
                fracture = fracture,
                listId = listId,
            )
        }
    }

    private fun Status.toDBEntity(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        fracture: Boolean,
        listId: String?,
    ): ActivityPubStatusTableEntity {
        return ActivityPubStatusTableEntity(
            id = this.id,
            role = role,
            type = type,
            createTimestamp = this.datetime,
            listId = listId,
            status = this,
            fracture = fracture,
        )
    }
}
