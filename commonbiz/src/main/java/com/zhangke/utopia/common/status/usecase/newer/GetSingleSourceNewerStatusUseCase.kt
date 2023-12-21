package com.zhangke.utopia.common.status.usecase.newer

import android.util.Log
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetSingleSourceNewerStatusUseCase @Inject constructor(
    private val getNewerStatusFromLocal: GetNewerStatusFromLocalUseCase,
    private val syncNewerStatus: SyncNewerStatusUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        minStatus: StatusContentEntity,
    ): Result<List<StatusContentEntity>> {
        Log.d("U_TEST", "GetSingleSourceNewerStatusUseCase($sourceUri, $limit, $minStatus")
        val statusFromLocal = getNewerStatusFromLocal(sourceUri, limit, minStatus)
        Log.d("U_TEST", "GetSingleSourceNewerStatusUseCase: statusFromLocal size is ${statusFromLocal.size}")
        if (statusFromLocal.size >= limit) return Result.success(statusFromLocal)
        val syncResult = syncNewerStatus(sourceUri, limit, minStatus)
        Log.d("U_TEST", "GetSingleSourceNewerStatusUseCase: sync result is $syncResult")
        if (syncResult.isFailure) return Result.failure(syncResult.exceptionOrNull()!!)
        return getNewerStatusFromLocal(sourceUri, limit, minStatus)
            .let { Result.success(it) }
            .also {
                Log.d("U_TEST", "GetSingleSourceNewerStatusUseCase: getNewerStatusFromLocal result success == ${it.isSuccess}, size is ${it.getOrNull()?.size}")
            }
    }
}
