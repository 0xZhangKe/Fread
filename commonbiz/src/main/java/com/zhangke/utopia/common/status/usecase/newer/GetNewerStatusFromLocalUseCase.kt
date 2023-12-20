package com.zhangke.utopia.common.status.usecase.newer

import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class GetNewerStatusFromLocalUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        minStatus: StatusContentEntity,
    ): Result<List<StatusContentEntity>> {
        return Result.success(statusContentRepo.queryNewer(sourceUri, minStatus.createTimestamp, limit))
    }
}
