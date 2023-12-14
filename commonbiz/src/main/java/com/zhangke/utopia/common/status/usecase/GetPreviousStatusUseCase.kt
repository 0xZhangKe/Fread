package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

class GetPreviousStatusUseCase @Inject internal constructor(
    private val alignmentStatus: AlignmentStatusUseCase,
    private val getPreviousStatusFromLocal: GetPreviousStatusFromLocalUseCase,
    private val getPreviousStatusFromServer: GetPreviousStatusFromServerUseCase,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String,
        limit: Int,
    ): Result<List<StatusContentEntity>> {
        val alignmentResult = alignmentStatus(feedsConfig.sourceUriList, sinceId)
        if (alignmentResult.isFailure) {
            return Result.failure(alignmentResult.exceptionOrThrow())
        }
        val statusListFromLocal = getPreviousStatusFromLocal(feedsConfig, sinceId, limit)
        if (statusListFromLocal.size >= limit) {
            return Result.success(statusListFromLocal)
        }
        return getPreviousStatusFromServer(feedsConfig, sinceId, limit)
    }
}
