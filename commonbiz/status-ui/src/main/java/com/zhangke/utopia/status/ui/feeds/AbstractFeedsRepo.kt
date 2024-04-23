package com.zhangke.utopia.status.ui.feeds

import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.status.status.model.Status

abstract class AbstractFeedsRepo {

    abstract suspend fun getLocalStatus(): Result<List<Status>>

    protected abstract suspend fun fetchRemoteStatus(maxId: String?): Result<List<Status>>

    protected abstract suspend fun replaceLocalStatus(statuses: List<Status>)

    protected abstract suspend fun appendLocalStatus(statuses: List<Status>)

    abstract suspend fun updateLocalStatus(status: Status)

    suspend fun refreshStatus(): Result<RefreshResult> {
        val result = fetchRemoteStatus(null)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }
        val localStatus = getLocalStatus().getOrNull()
        val remoteStatus = result.getOrThrow()
        val deletedStatus = mutableListOf<Status>()
        val localRecentStatus = localStatus?.maxByOrNull { it.datetime }
        if (localRecentStatus != null) {
            // 清理本地过期数据
            // 将新的数据更新到本地
            val localInNewIndex = remoteStatus.indexOfFirst {
                it.id == localRecentStatus.id
            }
            if (localInNewIndex < 0) {
                // 本地最新数据不在新数据中，表示本地数据已经过期
                replaceLocalStatus(emptyList())
                deletedStatus.addAll(localStatus)
            } else {
                // 新数据与本地数据有重合，清除本地数据重复部分，然后插入全部新数据
                val repeatedList = remoteStatus.subList(localInNewIndex, remoteStatus.size)
                deletedStatus.addAll(repeatedList)
            }
        }
        replaceLocalStatus(remoteStatus)
        return Result.success(RefreshResult(remoteStatus, deletedStatus))
    }

    suspend fun loadMoreStatus(maxId: String): Result<List<Status>> {
        val remoteStatusResult = fetchRemoteStatus(maxId)
        if (remoteStatusResult.isFailure) {
            return Result.failure(remoteStatusResult.exceptionOrNull()!!)
        }
        val remoteStatus = remoteStatusResult.getOrThrow()
        appendLocalStatus(remoteStatus)
        return Result.success(remoteStatus)
    }
}
