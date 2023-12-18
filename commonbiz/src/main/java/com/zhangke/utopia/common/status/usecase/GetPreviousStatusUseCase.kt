package com.zhangke.utopia.common.status.usecase

import android.util.Log
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
        maxId: String?,
        limit: Int = 50,
    ): Result<List<Status>> {
        Log.d("U_TEST", "GetPreviousStatusUseCase feeds is ${feedsConfig.name}, sinceId is ${maxId}")
        if (maxId != null) {
            val alignmentResult = alignmentStatus(feedsConfig.sourceUriList, maxId)
            if (alignmentResult.isFailure) {
                return Result.failure(alignmentResult.exceptionOrNull()!!)
            }
        }
        val statusList = getPreviousStatusFromLocalUseCase(feedsConfig, maxId, limit)
        if (statusList.size >= limit || areAllStatusIsTheirSourceFirst(statusList)) {
            return Result.success(statusList.map(statusContentEntityAdapter::toStatus))
        }
        return getPreviousStatusFromServerUseCase(feedsConfig, maxId, limit).map {
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
