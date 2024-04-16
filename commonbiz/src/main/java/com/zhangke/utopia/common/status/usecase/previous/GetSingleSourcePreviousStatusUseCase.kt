package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetSingleSourcePreviousStatusUseCase @Inject constructor(
    private val getPreviousStatusFromLocal: GetPreviousStatusFromLocalUseCase,
    private val syncPreviousStatus: SyncPreviousStatusUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        maxCreateTime: Long,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val statusFromLocal = getPreviousStatusFromLocal(sourceUri, limit, maxCreateTime)
        if (statusFromLocal.size >= limit ||
            statusFromLocal.lastOrNull()?.isFirstStatus == true
        ) {
            return Result.success(statusFromLocal)
        }
        val syncResult = syncPreviousStatus(sourceUri, limit, maxCreateTime)
        if (syncResult.isFailure) {
            return Result.failure(syncResult.exceptionOrNull()!!)
        }
        return getPreviousStatusFromLocal(sourceUri, limit, maxCreateTime)
            .let { Result.success(it) }
    }
}
