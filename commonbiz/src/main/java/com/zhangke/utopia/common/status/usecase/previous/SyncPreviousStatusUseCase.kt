package com.zhangke.utopia.common.status.usecase.previous

import android.util.Log
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.status.usecase.SaveStatusListToLocalUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class SyncPreviousStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    private val statusResolver get() = statusProvider.statusResolver

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        maxCreateTime: Long?,
    ): Result<Unit> {
        return syncStatusAndSaveToLocal(
            sourceUri = sourceUri,
            targetSize = limit,
            pageLimit = limit,
            maxStatus = maxCreateTime?.let { decideMaxStatus(sourceUri, maxCreateTime) })
    }

    private suspend fun syncStatusAndSaveToLocal(
        sourceUri: FormalUri,
        targetSize: Int,
        pageLimit: Int,
        maxStatus: StatusContentEntity?,
    ): Result<Unit> {
        val result = statusResolver.getStatusList(
            uri = sourceUri,
            limit = pageLimit,
            maxId = maxStatus?.statusIdOfPlatform,
        ).map { list ->
            statusContentEntityAdapter.toEntityList(sourceUri, list, null)
        }
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val statusList = result.getOrThrow()
        saveStatusListToLocal(
            statusList = statusList,
            maxId = maxStatus?.id,
            needCheckFirstStatus = statusList.size < pageLimit,
        )
        if (statusList.isEmpty()) return Result.success(Unit)
        val leftCount = targetSize - statusList.size
        if (leftCount > 0) {
            return syncStatusAndSaveToLocal(
                sourceUri,
                leftCount,
                pageLimit,
                statusList.minBy { it.createTimestamp })
        }
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
        ).also {
            Log.d(
                "U_TEST",
                "decideMaxStatus id is ${it?.statusIdOfPlatform}, content is ${it?.content}"
            )
        }
    }
}
