package com.zhangke.utopia.common.status.usecase.newer

import android.util.Log
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetNewerStatusUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
    private val getSingleSourceNewerStatus: GetSingleSourceNewerStatusUseCase,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        limit: Int,
        minStatusId: String,
    ): Result<List<Status>> {
        Log.d(
            "U_TEST",
            "GetNewerStatusUseCase(${sourceUriList.joinToString(",")}, $limit, $minStatusId"
        )
        val minStatus = statusContentRepo.queryByPlatformId(minStatusId)
        val minCreateTime = minStatus?.createTimestamp
        if (minCreateTime == null) {
            Log.d("U_TEST", "GetNewerStatusUseCase: Can't find record by id $minStatusId")
            return Result.failure(IllegalArgumentException("Can't find record by id $minStatusId"))
        }
        val resultList = sourceUriList.map { sourceUri ->
            getSingleSourceNewerStatus(
                sourceUri = sourceUri,
                limit = limit,
                minCreateTime = minCreateTime,
            )
        }
        if (resultList.all { it.isFailure }) {
            Log.d(
                "U_TEST",
                "GetNewerStatusUseCase: result all failure, case ${
                    resultList.first().exceptionOrNull()
                }"
            )
            return Result.failure(resultList.first().exceptionOrNull()!!)
        }
        val statusList = resultList.flatMap { it.getOrNull() ?: emptyList() }
            .filter { it.id != minStatus.id }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
            .map(statusContentEntityAdapter::toStatus)
        Log.d("U_TEST", "GetNewerStatusUseCase: get success, size is ${statusList.size}")
        return Result.success(statusList)
    }
}
