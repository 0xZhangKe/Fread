package com.zhangke.fread.activitypub.app.internal.repo.status

import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.status.model.Status

class ActivityPubTimelineStatusRepo (
    activityPubStatusDatabases: ActivityPubStatusDatabases,
    private val getTimeline: GetTimelineStatusUseCase,
) {

    private val statusDao = activityPubStatusDatabases.getDao()
    private val statusConfig = StatusConfigurationDefault.config

    /**
     * 获取最新的帖子列表
     */
    suspend fun getFresherStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String? = null,
        limit: Int = statusConfig.loadFromServerLimit,
    ): Result<List<Status>> {
        return getTimeline(
            locator = locator,
            type = type,
            limit = limit,
            listId = listId,
            maxId = null,
            minId = null,
        ).onSuccess {
            saveFresherStatus(
                locator = locator,
                type = type,
                listId = listId,
                statusList = it,
            )
        }
    }

    private suspend fun saveFresherStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statusList: List<Status>,
    ) {
        if (statusList.isEmpty()) return
        val earlierStatus = statusList.minBy { it.createAt.epochMillis }
        val localEarlierStatus = queryLocalStatus(
            locator = locator,
            type = type,
            listId = listId,
            statusId = earlierStatus.id,
        )
        if (localEarlierStatus == null) {
            // local data are expired
            deleteStatus(locator, type, listId)
        }
        statusList.insertToLocal(locator, type, listId)
    }

    suspend fun loadPreviousPageStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        minId: String,
        listId: String? = null,
        limit: Int = statusConfig.loadFromServerLimit,
    ): Result<List<Status>> {
        return getTimeline(
            locator = locator,
            type = type,
            limit = limit,
            maxId = null,
            minId = minId,
            listId = listId,
        ).onSuccess {
            it.insertToLocal(locator, type, listId)
        }
    }

    suspend fun getStatusFromLocal(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        limit: Int = statusConfig.loadFromLocalLimit,
        listId: String? = null,
    ): List<Status> {
        return queryLocalStatusList(
            locator = locator,
            type = type,
            limit = limit,
            listId = listId,
        ).map { it.status }
    }

    suspend fun loadMore(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String? = null,
        limit: Int = statusConfig.loadFromLocalLimit,
    ): Result<List<Status>> {
        return getTimeline(
            locator = locator,
            type = type,
            limit = limit,
            maxId = maxId,
            minId = null,
            listId = listId,
        ).onSuccess {
            it.insertToLocal(locator, type, listId)
        }
    }

    suspend fun updateStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        status: Status,
        listId: String? = null,
    ) {
        val localStatus = queryLocalStatus(
            locator = locator,
            type = type,
            listId = listId,
            statusId = status.id,
        )
        if (localStatus != null) {
            statusDao.insert(status.toDBEntity(locator, type, listId))
        }
        val leftTypes = ActivityPubStatusSourceType.entries.filter { it != type }
        leftTypes.mapNotNull {
            queryLocalStatus(
                locator = locator,
                type = it,
                listId = listId,
                statusId = status.id,
            )
        }.forEach {
            statusDao.insert(status.toDBEntity(locator, it.type, it.listId))
        }
    }

    suspend fun deleteStatus(statusId: String) {
        statusDao.delete(statusId)
    }

    private suspend fun List<Status>.insertToLocal(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ) {
        statusDao.insert(this.toDBEntities(locator, type, listId))
    }

    private suspend fun queryLocalStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statusId: String,
    ): ActivityPubStatusTableEntity? {
        return if (type == ActivityPubStatusSourceType.LIST && listId != null) {
            statusDao.queryStatusInList(
                locator = locator,
                type = type,
                listId = listId,
                id = statusId,
            )
        } else {
            statusDao.query(locator, type, statusId)
        }
    }

    private suspend fun queryLocalStatusList(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String?,
    ): List<ActivityPubStatusTableEntity> {
        return if (type == ActivityPubStatusSourceType.LIST) {
            statusDao.queryListStatus(
                locator = locator,
                type = type,
                limit = limit,
                listId = listId!!,
            )
        } else {
            statusDao.queryTimelineStatus(locator, type, limit)
        }
    }

    private suspend fun deleteStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ) {
        if (listId.isNullOrEmpty()) {
            statusDao.delete(locator, type)
        } else {
            statusDao.deleteListStatus(locator, type, listId)
        }
    }

    private fun List<Status>.toDBEntities(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ): List<ActivityPubStatusTableEntity> {
        return this.map {
            it.toDBEntity(
                locator = locator,
                type = type,
                listId = listId,
            )
        }
    }

    private fun Status.toDBEntity(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ): ActivityPubStatusTableEntity {
        return ActivityPubStatusTableEntity(
            id = this.id,
            locator = locator,
            type = type,
            createTimestamp = this.createAt.epochMillis,
            listId = listId.orEmpty(),
            status = this,
        )
    }
}