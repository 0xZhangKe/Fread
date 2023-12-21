package com.zhangke.utopia.common.status.usecase.previous

import android.util.Log
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.common.utils.isFirstStatus
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetSingleSourcePreviousStatusUseCase @Inject constructor(
    private val getPreviousStatusFromLocal: GetPreviousStatusFromLocalUseCase,
    private val syncPreviousStatus: SyncPreviousStatusUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        maxCreateTime: Long?,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        Log.d("U_TEST", "GetSingleSourcePreviousStatusUseCase($sourceUri, $maxCreateTime, $limit")
        val statusFromLocal = getPreviousStatusFromLocal(sourceUri, limit, maxCreateTime)
        Log.d(
            "U_TEST",
            "GetSingleSourcePreviousStatusUseCase() statusFromLocal result size is ${statusFromLocal.size}"
        )
        if (statusFromLocal.size >= limit ||
            statusFromLocal.lastOrNull()?.isFirstStatus() == true
        ) {
            return Result.success(statusFromLocal)
        }
        val syncResult = syncPreviousStatus(sourceUri, limit, maxCreateTime)
        if (syncResult.isFailure) {
            Log.d("U_TEST", "GetSingleSourcePreviousStatusUseCase: sync result is $syncResult")
            return Result.failure(syncResult.exceptionOrNull()!!)
        }
        return getPreviousStatusFromLocal(sourceUri, limit, maxCreateTime)
            .let { Result.success(it) }
    }
}
