package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusContentDao
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StatusContentRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
    private val sourceContentEntityAdapter: StatusContentEntityAdapter,
) {

    private val statusContentDao: StatusContentDao get() = statusDatabase.getStatusContentDao()

    suspend fun queryBySourceUri(sourceUri: StatusProviderUri): List<Status> {
        return statusContentDao.queryBySourceUri(sourceUri)
            .map(sourceContentEntityAdapter::toStatus)
    }

    fun queryBySourceUriList(
        sourceUriList: List<StatusProviderUri>
    ): Flow<List<Status>> {
        return statusContentDao.queryBySourceUriList(sourceUriList)
            .map { it.map(sourceContentEntityAdapter::toStatus) }
    }

    suspend fun insert(statusSourceUri: StatusProviderUri, status: Status) {
        statusContentDao.insert(sourceContentEntityAdapter.toEntity(statusSourceUri, status))
    }

    suspend fun insert(statusSourceUri: StatusProviderUri, statusList: List<Status>) {
        statusList.map { sourceContentEntityAdapter.toEntity(statusSourceUri, it) }
            .let { statusContentDao.insert(it) }
    }

    suspend fun deleteById(id: Long) {
        statusContentDao.deleteById(id)
    }
}
