package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.status.usecase.SaveStatusListToLocalUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class SyncPreviousStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    private val statusResolver get() = statusProvider.statusResolver

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        maxStatus: StatusContentEntity?,
    ): Result<Unit> {
        return if (maxStatus == null) {
            getStatus(sourceUri, limit)
        } else {
            Result.success(Unit)
        }
    }

    private suspend fun getStatus(
        sourceUri: FormalUri,
        limit: Int,
    ): Result<Unit> {
        val result = statusResolver.getStatusList(sourceUri, limit)
            .map { list ->
                statusContentEntityAdapter.toEntityList(sourceUri, list, null)
            }
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val statusList = result.getOrNull()
        if (statusList.isNullOrEmpty()) return Result.success(Unit)
        var nextIdOfLatest: String? = null
        if (statusList.size < limit) {
            val latestStatus = statusList.last()
            val isFirstStatus = statusResolver.checkIsFirstStatus(
                sourceUri = sourceUri,
                statusId = latestStatus.statusIdOfPlatform,
            ).getOrNull() == true
            //接着加载下一页，直到满足 limit
            nextIdOfLatest = if (isFirstStatus) {
                StatusContentRepo.STATUS_END_MAGIC_NUMBER
            } else {
                null
            }
        }
        saveStatusListToLocal(
            statusList = statusList,
            maxId = null,
            nextIdOfLatest = nextIdOfLatest,
        )
        return Result.success(Unit)
    }
}
