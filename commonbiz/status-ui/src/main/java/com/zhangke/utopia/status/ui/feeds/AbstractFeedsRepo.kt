package com.zhangke.utopia.status.ui.feeds

import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.status.status.model.Status

abstract class AbstractFeedsRepo {

    abstract suspend fun getLocalStatus(): List<Status>

    protected abstract suspend fun fetchRemoteStatus(maxId: String?): Result<List<Status>>

    protected abstract suspend fun replaceLocalStatus(statuses: List<Status>)

    abstract suspend fun appendLocalStatus(statuses: List<Status>)

    suspend fun refreshStatus(): Result<RefreshResult>{
        val localStatus = getLocalStatus()
        val result = fetchRemoteStatus(null)
        if (result.isFailure){
            return Result.failure(result.exceptionOrNull()!!)
        }
        val remoteStatus = result.getOrThrow()

    }
}
