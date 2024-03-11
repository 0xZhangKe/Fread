package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.repo.db.StatusContentDao
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class StatusContentRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
) {

    companion object {

        const val STATUS_END_MAGIC_NUMBER = "status_end_42"
    }

    private val statusContentDao: StatusContentDao get() = statusDatabase.getStatusContentDao()

    suspend fun query(sourceUri: FormalUri): List<StatusContentEntity> {
        return statusContentDao.queryBySource(sourceUri)
    }

    suspend fun query(sourceUri: FormalUri, limit: Int): List<StatusContentEntity> {
        return statusContentDao.queryBySource(sourceUri, limit)
    }

    suspend fun query(id: String): StatusContentEntity? {
        return statusContentDao.query(id)
    }

    suspend fun query(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.queryBySource(sourceUriList, limit)
    }

    suspend fun query(
        sourceUriList: List<FormalUri>,
    ): List<StatusContentEntity> {
        return statusContentDao.queryBySource(sourceUriList)
    }

    suspend fun queryByPlatformId(
        statusIdOfPlatform: String,
    ): StatusContentEntity? {
        return statusContentDao.queryByPlatformId(statusIdOfPlatform)
    }

    suspend fun queryPrevious(
        sourceUri: FormalUri,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.queryPrevious(
            sourceUri = sourceUri,
            createTimestamp = createTimestamp,
            limit = limit,
        )
    }

    suspend fun queryNewer(
        sourceUri: FormalUri,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity> {
        return statusContentDao.queryNewer(
            sourceUri = sourceUri,
            createTimestamp = createTimestamp,
            limit = limit,
        )
    }

    suspend fun queryRecentPrevious(
        sourceUri: FormalUri,
        createTimestamp: Long,
    ): StatusContentEntity? {
        return statusContentDao.queryRecentPrevious(
            sourceUri = sourceUri,
            createTimestamp = createTimestamp,
        ).maxByOrNull { it.createTimestamp }
    }

    suspend fun queryRecentNewer(
        sourceUri: FormalUri,
        createTimestamp: Long,
    ): StatusContentEntity? {
        return statusContentDao.queryRecentNewer(
            sourceUri = sourceUri,
            createTimestamp = createTimestamp,
        ).minByOrNull { it.createTimestamp }
    }

    suspend fun insert(status: StatusContentEntity) {
        statusContentDao.insert(status)
    }

    suspend fun insert(statusList: List<StatusContentEntity>) {
        statusContentDao.insert(statusList)
    }

    suspend fun updateAuthor(author: BlogAuthor) {
        statusContentDao.queryAll()
            .filter {
                when (it.status) {
                    is Status.NewBlog -> {
                        it.status.blog.author.uri == author.uri
                    }

                    is Status.Reblog -> {
                        it.status.author.uri == author.uri || it.status.reblog.author.uri == author.uri
                    }
                }
            }.map {
                it.copy(status = it.status.updateAuthor(author))
            }.let {
                statusContentDao.insert(it)
            }
    }

    private fun Status.updateAuthor(author: BlogAuthor): Status {
        return when (this) {
            is Status.NewBlog -> {
                this.copy(
                    blog = this.blog.copy(author = author)
                )
            }

            is Status.Reblog -> {
                if (this.author.uri == author.uri) {
                    this.copy(author = author)
                } else if (this.reblog.author.uri == author.uri) {
                    this.copy(reblog = this.reblog.copy(author = author))
                } else {
                    this
                }
            }
        }
    }

    suspend fun deleteById(id: Long) {
        statusContentDao.deleteById(id)
    }
}
