package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusContentDao
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

internal class StatusContentRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
    private val sourceContentEntityAdapter: StatusContentEntityAdapter,
) {

    companion object {

        const val STATUS_END_MAGIC_NUMBER = "status_end_42"
    }

    private val statusContentDao: StatusContentDao get() = statusDatabase.getStatusContentDao()

    suspend fun queryBySourceUri(sourceUri: StatusProviderUri): List<StatusContentEntity> {
        return statusContentDao.queryBySourceUri(sourceUri)
    }

    suspend fun querySourceById(id: String): StatusContentEntity? {
        return statusContentDao.queryById(id)
    }

    suspend fun queryBySourceUriListBefore(
        sourceUriList: List<StatusProviderUri>,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.queryBySourceUriListBefore(
            sourceUriList = sourceUriList,
            createTimestamp = createTimestamp,
            limit = limit,
        )
    }

    suspend fun queryBySourceUriListAfter(
        sourceUriList: List<StatusProviderUri>,
        createTimestamp: Long,
        limit: Int? = null
    ): List<StatusContentEntity> {
        return if (limit != null) {
            statusContentDao.queryBySourceUriListAfter(
                sourceUriList = sourceUriList,
                createTimestamp = createTimestamp,
                limit = limit,
            )
        } else {
            statusContentDao.queryBySourceUriListAfter(
                sourceUriList = sourceUriList,
                createTimestamp = createTimestamp,
            )
        }
    }

    suspend fun queryBySourceUriList(
        sourceUriList: List<StatusProviderUri>,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.queryBySourceUriList(sourceUriList, limit)
    }

    suspend fun insert(status: StatusContentEntity) {
        statusContentDao.insert(status)
    }

    suspend fun insert(statusList: List<StatusContentEntity>) {
        statusContentDao.insert(statusList)
    }

    suspend fun deleteById(id: Long) {
        statusContentDao.deleteById(id)
    }
}
