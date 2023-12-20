package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetPreviousStatusUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
    private val getSingleSourcePreviousStatus: GetSingleSourcePreviousStatusUseCase,
) {

    suspend operator fun invoke(
        sourceUriList: List<FormalUri>,
        limit: Int,
        maxId: String?,
    ): Result<List<StatusContentEntity>> {
        val maxStatus = if (maxId.isNullOrEmpty()) {
            null
        } else {
            statusContentRepo.query(maxId)
                ?: return Result.failure(IllegalArgumentException("Can't find record by id $maxId"))
        }
        val resultList = sourceUriList.map { sourceUri ->
            getSingleSourcePreviousStatus(
                sourceUri = sourceUri,
                limit = limit,
                maxStatus = maxStatus,
            )
        }
        if (resultList.all { it.isFailure }) return Result.failure(resultList.first().exceptionOrNull()!!)
        val statusList = resultList.flatMap { it.getOrNull() ?: emptyList() }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
        return Result.success(statusList)
    }
}
