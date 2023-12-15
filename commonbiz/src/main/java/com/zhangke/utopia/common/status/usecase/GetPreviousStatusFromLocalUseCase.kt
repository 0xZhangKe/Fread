package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

internal class GetPreviousStatusFromLocalUseCase @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String,
        limit: Int,
    ): List<StatusContentEntity> {
        val sinceStatus = statusContentRepo.query(sinceId) ?: return emptyList()
        return statusContentRepo.queryAfter(
            sourceUriList = feedsConfig.sourceUriList,
            createTimestamp = sinceStatus.createTimestamp,
        ).filter { it.id != sinceId }
            .groupBy { it.sourceUri }
            .flatMap { (_, statusList) ->
                val index = statusList.indexOfLast { it.nextStatusId.isNullOrEmpty() }
                if (index >= 0) {
                    val startIndex = index + 1
                    if (startIndex > statusList.lastIndex) {
                        emptyList()
                    } else {
                        statusList.subList(startIndex, statusList.size)
                    }
                } else {
                    statusList
                }
            }
            .sortedByDescending { it.createTimestamp }
            .takeLast(limit)
    }
}
