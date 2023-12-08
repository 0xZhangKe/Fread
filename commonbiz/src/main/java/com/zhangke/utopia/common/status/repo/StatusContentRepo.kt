package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusContentDao
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
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

    suspend fun queryBySourceUriList(
        sourceUriList: List<StatusProviderUri>
    ): List<Status> {
        return statusContentDao.queryBySourceUriList(sourceUriList)
            .map(sourceContentEntityAdapter::toStatus)
    }

    suspend fun insert(statusSourceUri: StatusProviderUri, status: Status) {
        statusContentDao.insert(sourceContentEntityAdapter.toEntity(statusSourceUri, status))
    }

    suspend fun deleteById(id: Long) {
        statusContentDao.deleteById(id)
    }
}
