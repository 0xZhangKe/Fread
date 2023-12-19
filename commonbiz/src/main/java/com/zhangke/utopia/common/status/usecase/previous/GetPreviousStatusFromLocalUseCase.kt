package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetPreviousStatusFromLocalUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        maxId: String?,
    ): Result<List<StatusContentEntity>> {
        if (maxId.isNullOrEmpty()) {
            return Result.success(statusContentRepo.query(sourceUri, limit))
        }
        val maxStatus = statusContentRepo.query(maxId) ?: return Result.failure(IllegalArgumentException("Can't find record by id $maxId"))
        return statusContentRepo.queryPrevious(sourceUri, maxStatus.createTimestamp, limit)
            .let { Result.success(it) }
    }
}
