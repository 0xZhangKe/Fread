package com.zhangke.utopia.common.status.usecase.previous

import android.util.Log
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetSingleSourcePreviousStatusUseCase @Inject constructor(
    private val getPreviousStatusFromLocal: GetPreviousStatusFromLocalUseCase,
    private val syncPreviousStatus: SyncPreviousStatusUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        maxStatus: StatusContentEntity?,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        Log.d("U_TEST", "GetSingleSourcePreviousStatusUseCase($sourceUri, $maxStatus, $limit")
        val statusFromLocalResult = getPreviousStatusFromLocal(sourceUri, limit, maxStatus)
        Log.d(
            "U_TEST",
            "GetSingleSourcePreviousStatusUseCase() statusFromLocalResult success == ${statusFromLocalResult.isSuccess}, result size is ${statusFromLocalResult.getOrNull()?.size}"
        )
        if (statusFromLocalResult.isSuccess) {
            val statusFromLocal = statusFromLocalResult.getOrNull()
            if (statusFromLocal != null && statusFromLocal.size >= limit) {
                return Result.success(statusFromLocal)
            }
        }
        val syncResult = syncPreviousStatus(sourceUri, limit, maxStatus)
        if (syncResult.isFailure) {
            Log.d("U_TEST", "GetSingleSourcePreviousStatusUseCase: sync result is $syncResult")
            return Result.failure(syncResult.exceptionOrNull()!!)
        }
        return getPreviousStatusFromLocal(sourceUri, limit, maxStatus).also {
            Log.d("U_TEST", "GetSingleSourcePreviousStatusUseCase from local result success == ${it.isSuccess}, size is ${it.getOrNull()?.size}")
        }
    }
}
