package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDao
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabase
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class ActivityPubStatusRepo @Inject constructor(
    private val statusDatabase: ActivityPubStatusDatabase,
) {

    private val statusDao: ActivityPubStatusDao get() = statusDatabase.getDao()

    suspend fun query(role: IdentityRole, id: String): Status? {
        return statusDao.query(role, id)?.status
    }

    suspend fun query(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        limit: Int,
    ): List<Status> {
        return if (!listId.isNullOrEmpty() && type == ActivityPubStatusSourceType.LIST) {
            statusDao.queryListStatus(role, ActivityPubStatusSourceType.LIST, listId, limit)
        } else {
            statusDao.query(role, type, limit)
        }.map { it.status }
    }

    suspend fun insertOrReplace(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        status: Status,
    ) {
        statusDao.insert(
            ActivityPubStatusTableEntity(
                id = status.id,
                role = role,
                type = type,
                listId = listId,
                status = status,
                createTimestamp = status.datetime,
            )
        )
    }

    suspend fun deleteStatus(role: IdentityRole, type: ActivityPubStatusSourceType) {
        statusDao.delete(role, type)
    }

    suspend fun deleteListStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String,
    ) {
        statusDao.deleteListStatus(role, type, listId)
    }

    suspend fun insertOrReplace(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<Status>,
    ) {
        statusDao.insert(
            statuses.map {
                ActivityPubStatusTableEntity(
                    id = it.id,
                    role = role,
                    type = type,
                    listId = listId,
                    status = it,
                    createTimestamp = it.datetime,
                )
            }
        )
    }

    suspend fun updateStatus(role: IdentityRole, status: Status) {
        val originEntity = statusDao.query(role, status.id) ?: return
        statusDao.insert(
            ActivityPubStatusTableEntity(
                id = status.id,
                role = role,
                type = originEntity.type,
                listId = originEntity.listId,
                status = status,
                createTimestamp = status.datetime,
            )
        )
    }

    suspend fun updatePoll(role: IdentityRole, id: String, poll: BlogPoll) {
        val originEntity = statusDao.query(role, id) ?: return
        val newStatus = when (val status = originEntity.status) {
            is Status.NewBlog -> {
                status.copy(
                    blog = status.blog.copy(poll = poll),
                )
            }

            is Status.Reblog -> {
                status.copy(
                    reblog = status.reblog.copy(poll = poll),
                )
            }
        }
        statusDao.insert(originEntity.copy(status = newStatus))
    }

    suspend fun queryRecentListStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
    ): Status? {
        return if (listId == null) {
            statusDao.queryRecentStatus(
                role = role,
                type = type,
            )?.status
        } else {
            statusDao.queryRecentListStatus(
                role = role,
                type = type,
                listId = listId,
            )?.status
        }
    }
}
