package com.zhangke.utopia.common.status.usecase.previous

import android.util.Log
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.utils.isFirstStatus
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetPreviousStatusUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
    private val getSingleSourcePreviousStatus: GetSingleSourcePreviousStatusUseCase,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        limit: Int,
        maxId: String?,
    ): Result<List<Status>> {
        Log.d(
            "U_TEST",
            "GetPreviousStatusUseCase(${sourceUriList.joinToString(",")}, $limit, $maxId"
        )
        val maxStatus = if (maxId.isNullOrEmpty()) {
            null
        } else {
            statusContentRepo.queryByPlatformId(maxId)
                ?: return Result.failure(IllegalArgumentException("Can't find record by id $maxId"))
        }
        if (maxStatus?.isFirstStatus() == true) {
            return Result.success(emptyList())
        }
        val maxCreateTime = maxStatus?.createTimestamp
        val resultList = sourceUriList.map { sourceUri ->
            getSingleSourcePreviousStatus(
                sourceUri = sourceUri,
                limit = limit,
                maxCreateTime = maxCreateTime,
            )
        }
        if (resultList.all { it.isFailure }) {
            Log.d(
                "U_TEST",
                "GetPreviousStatusUseCase: result all failure, case ${
                    resultList.first().exceptionOrNull()
                }"
            )
            return Result.failure(resultList.first().exceptionOrNull()!!)
        }
        val statusList = resultList.flatMap { it.getOrNull() ?: emptyList() }
            .filter { it.id != maxStatus?.id }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
            .map(statusContentEntityAdapter::toStatus)
        Log.d("U_TEST", "GetPreviousStatusUseCase: get success, size is ${statusList.size}")
        return Result.success(statusList)
    }
}
