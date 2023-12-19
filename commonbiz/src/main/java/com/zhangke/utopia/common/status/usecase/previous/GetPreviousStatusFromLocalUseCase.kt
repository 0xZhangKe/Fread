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
        maxStatus: StatusContentEntity?,
    ): Result<List<StatusContentEntity>> {
        val statusList = if (maxStatus == null) {
            statusContentRepo.query(sourceUri, limit)
        } else {
            statusContentRepo.queryPrevious(sourceUri, maxStatus.createTimestamp, limit)
        }
        return Result.success(statusList)
    }
}
