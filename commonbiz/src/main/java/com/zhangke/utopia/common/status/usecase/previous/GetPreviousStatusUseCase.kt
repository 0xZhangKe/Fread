package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetPreviousStatusUseCase @Inject constructor(
    private val getPreviousStatusFromLocal: GetPreviousStatusFromLocalUseCase,
    private val syncPreviousStatus: SyncPreviousStatusUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        maxId: String?,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val statusFromLocalResult = getPreviousStatusFromLocal(sourceUri, limit, maxId)
        if (statusFromLocalResult.isSuccess) {
            val statusFromLocal = statusFromLocalResult.getOrNull()
            if (statusFromLocal != null && statusFromLocal.size >= limit) {
                return Result.success(statusFromLocal)
            }
        }
        val syncResult = syncPreviousStatus(sourceUri, limit, maxId)
        if (syncResult.isFailure) return Result.failure(syncResult.exceptionOrNull()!!)
        return getPreviousStatusFromLocal(sourceUri, limit, maxId)
    }
}
