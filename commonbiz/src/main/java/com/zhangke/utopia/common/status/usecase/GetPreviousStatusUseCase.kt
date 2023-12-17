package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetPreviousStatusUseCase @Inject internal constructor(
    private val alignmentStatus: AlignmentStatusUseCase,
    private val getPreviousStatusFromLocalUseCase: GetPreviousStatusFromLocalUseCase,
    private val getPreviousStatusFromServerUseCase: GetPreviousStatusFromServerUseCase,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int = 50,
    ): Result<List<Status>> {
        if (sinceId != null) {
            val alignmentResult = alignmentStatus(feedsConfig.sourceUriList, sinceId)
            if (alignmentResult.isFailure) {
                return Result.failure(alignmentResult.exceptionOrNull()!!)
            }
        }
        val statusList = getPreviousStatusFromLocalUseCase(feedsConfig, sinceId, limit)
        if (statusList.size >= limit || areAllStatusIsTheirSourceFirst(statusList)) {
            return Result.success(statusList.map(statusContentEntityAdapter::toStatus))
        }
        return getPreviousStatusFromServerUseCase(feedsConfig, sinceId, limit).map {
            it.map(statusContentEntityAdapter::toStatus)
        }
    }

    private fun areAllStatusIsTheirSourceFirst(statusList: List<StatusContentEntity>): Boolean {
        if (statusList.isEmpty()) return false
        return statusList.groupBy { it.sourceUri }
            .map { it.value.lastOrNull()?.nextStatusId == StatusContentRepo.STATUS_END_MAGIC_NUMBER }
            .reduce { acc, b -> acc && b }
    }
}
