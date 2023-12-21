package com.zhangke.utopia.common.status.usecase.newer

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.status.usecase.SaveStatusListToLocalUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class SyncNewerStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        sinceStatus: StatusContentEntity,
    ): Result<Unit> {
        return syncStatusAndSaveToLocal(
            sourceUri = sourceUri,
            targetSize = limit,
            pageLimit = limit,
            sinceStatus = sinceStatus,
        )
    }

    private suspend fun syncStatusAndSaveToLocal(
        sourceUri: FormalUri,
        targetSize: Int,
        pageLimit: Int,
        sinceStatus: StatusContentEntity?,
    ): Result<Unit> {
        val statusList = statusProvider.statusResolver
            .getStatusList(
                uri = sourceUri,
                limit = pageLimit,
                sinceId = sinceStatus?.statusIdOfPlatform,
            ).map { list ->
                statusContentEntityAdapter.toEntityList(sourceUri, list, sinceStatus?.id)
            }
        if (statusList.isFailure) return Result.failure(statusList.exceptionOrNull()!!)
        val statusEntityList = statusList.getOrThrow()
        if (statusEntityList.isEmpty()) return Result.success(Unit)
        saveStatusListToLocal(
            statusList = statusEntityList,
            maxId = null,
            needCheckFirstStatus = false,
        )
        val leftCount = targetSize - statusEntityList.size
        if (leftCount <= 0) return Result.success(Unit)
        return syncStatusAndSaveToLocal(sourceUri, leftCount, pageLimit, statusEntityList.maxBy { it.createTimestamp })
    }
}
