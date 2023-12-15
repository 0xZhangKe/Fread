package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusContentDao
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class StatusContentRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
    private val sourceContentEntityAdapter: StatusContentEntityAdapter,
) {

    companion object {

        const val STATUS_END_MAGIC_NUMBER = "status_end_42"
    }

    private val statusContentDao: StatusContentDao get() = statusDatabase.getStatusContentDao()

    suspend fun query(sourceUri: FormalUri): List<StatusContentEntity> {
        return statusContentDao.query(sourceUri)
    }

    suspend fun query(id: String): StatusContentEntity? {
        return statusContentDao.query(id)
    }

    suspend fun queryBefore(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
    ): List<StatusContentEntity> {
        return statusContentDao.queryBefore(
            sourceUriList = sourceUriList,
            createTimestamp = createTimestamp,
        )
    }

    suspend fun queryBefore(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.queryBefore(
            sourceUriList = sourceUriList,
            createTimestamp = createTimestamp,
            limit = limit,
        )
    }

    suspend fun queryAfter(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
        limit: Int? = null
    ): List<StatusContentEntity> {
        return if (limit != null) {
            statusContentDao.queryAfter(
                sourceUriList = sourceUriList,
                createTimestamp = createTimestamp,
                limit = limit,
            )
        } else {
            statusContentDao.queryAfter(
                sourceUriList = sourceUriList,
                createTimestamp = createTimestamp,
            )
        }
    }

    suspend fun query(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.query(sourceUriList, limit)
    }

    suspend fun query(
        sourceUriList: List<FormalUri>,
    ): List<StatusContentEntity> {
        return statusContentDao.query(sourceUriList)
    }

    suspend fun queryFirst(sourceUri: FormalUri): StatusContentEntity? {
        return statusContentDao.queryFirst(sourceUri)
    }

    suspend fun queryLatest(sourceUri: FormalUri): StatusContentEntity? {
        return statusContentDao.queryLatest(sourceUri)
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
