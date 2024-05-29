package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status

abstract class StatusRepo(
    private val apStatusRepo: ExpiredActivityPubStatusRepo,
) {

    protected abstract suspend fun loadStatusFromServer(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String?,
        limit: Int,
        listId: String? = null,
    ): Result<List<Status>>

    protected suspend fun getLocalStatusInternal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String? = null,
    ): List<Status> {
        return queryListFromLocal(
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
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
        listId: String? = null,
    ): Result<List<Status>> {
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

    suspend fun refreshStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
        listId: String? = null,
    ): Result<RefreshResult> {
        val serverStatusResult = loadStatusFromServer(
            role = role,
            type = type,
            maxId = null,
            limit = limit,
            listId = listId,
        )
        if (serverStatusResult.isFailure) {
            return Result.failure(serverStatusResult.exceptionOrNull()!!)
        }
        val remoteStatus = serverStatusResult.getOrThrow()
        val recentLocalStatus = apStatusRepo.queryRecentListStatus(
            role = role,
            type = type,
            listId = listId,
        )
        val deletedStatus = mutableListOf<Status>()
        if (recentLocalStatus != null) {
            // 清理本地过期数据
            // 将新的数据更新到本地
            val localInNewIndex = remoteStatus.indexOfFirst {
                it.id == recentLocalStatus.id
            }
            if (localInNewIndex < 0) {
                // 本地最新数据不在新数据中，表示本地数据已经过期
                val localStatus = queryListFromLocal(
                    role = role,
                    type = type,
                    limit = Int.MAX_VALUE,
                    listId = listId,
                )
                replaceLocalStatus(
                    role = role,
                    type = type,
                    listId = listId,
                    statuses = remoteStatus,
                )
                deletedStatus.addAll(localStatus)
            } else {
                // 新数据与本地数据有重合，清除本地数据重复部分，然后插入全部新数据
                val repeatedList = remoteStatus.subList(localInNewIndex, remoteStatus.size)
                deletedStatus.addAll(repeatedList)
                insertStatues(
                    role = role,
                    type = type,
                    listId = listId,
                    statuses = remoteStatus,
                )
            }
        } else {
            insertStatues(
                role = role,
                type = type,
                listId = listId,
                statuses = remoteStatus,
            )
        }
        return Result.success(
            RefreshResult(
                newStatus = remoteStatus,
                deletedStatus = deletedStatus,
            )
        )
    }

    private suspend fun queryListFromLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        listId: String?,
    ): List<Status> {
        return apStatusRepo.query(
            role = role,
            type = type,
            listId = listId,
            limit = limit,
        )
    }

    private suspend fun appendToLocal(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        maxId: String,
        listId: String?,
        statuses: List<Status>,
    ) {
        if (apStatusRepo.query(role, maxId) == null) return
        insertStatues(role, type, listId, statuses)
    }

    private suspend fun replaceLocalStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<Status>,
    ) {
        if (listId.isNullOrEmpty().not() && type == ActivityPubStatusSourceType.LIST) {
            apStatusRepo.deleteListStatus(role, type, listId!!)
        } else {
            apStatusRepo.deleteStatus(role, type)
        }
        apStatusRepo.insertOrReplace(
            role = role,
            type = type,
            listId = listId,
            statuses = statuses,
        )
    }

    private suspend fun insertStatues(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?,
        statuses: List<Status>,
    ) {
        apStatusRepo.insertOrReplace(
            role = role,
            type = type,
            listId = listId,
            statuses = statuses,
        )
    }

    suspend fun updateStatus(role: IdentityRole, status: Status) {
        apStatusRepo.updateStatus(role, status)
    }

    suspend fun updatePoll(role: IdentityRole, id: String, poll: BlogPoll) {
        apStatusRepo.updatePoll(role, id, poll)
    }
}
