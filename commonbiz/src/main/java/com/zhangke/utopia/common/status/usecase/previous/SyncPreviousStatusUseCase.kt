package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

/**
 * 通过服务端拉去一页数据，并存入本地数据库。
 */
internal class SyncPreviousStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    private val statusResolver get() = statusProvider.statusResolver

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        maxCreateTime: Long,
    ): Result<Unit> {
        return syncStatusAndSaveToLocal(
            sourceUri = sourceUri,
            pageLimit = limit,
            maxStatus = decideMaxStatus(sourceUri, maxCreateTime)
        )
    }

    private suspend fun syncStatusAndSaveToLocal(
        sourceUri: FormalUri,
        pageLimit: Int,
        maxStatus: StatusContentEntity?,
    ): Result<Unit> {
        val result = statusResolver.getStatusList(
            uri = sourceUri,
            limit = pageLimit,
            maxId = maxStatus?.statusIdOfPlatform,
        )
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val statusList = result.getOrThrow()
        if (statusList.isEmpty()) {
            if (maxStatus != null) {
                // 表示已经到底了
                statusContentRepo.markFirstStatus(sourceUri)
            }
            return Result.success(Unit)
        }
        val entities = statusList.map {
            statusContentEntityAdapter.toEntity(sourceUri, it, false)
        }
        statusContentRepo.insert(entities)
        return Result.success(Unit)
    }

    private suspend fun decideMaxStatus(
        sourceUri: FormalUri,
        maxCreateTime: Long,
    ): StatusContentEntity? {
        // 这里获取从该时间点开始最近的切更新的一条记录，因为要通过这个记录开始获取更早的数据
        return statusContentRepo.queryRecentNewer(
            sourceUri = sourceUri,
            createTimestamp = maxCreateTime,
        )
    }
}
