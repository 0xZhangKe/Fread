package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

internal class GetStatusFromLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int,
    ): List<StatusContentEntity> {
        val list = if (sinceId.isNullOrEmpty()) {
            getStatus(feedsConfig, limit)
        } else {
            val statusList = getStatusBeforeSinceId(feedsConfig, sinceId, limit)
            statusList.ifEmpty {
                getStatus(feedsConfig, limit)
            }
        }
        val groupedList = list.groupBy { it.sourceUri }
        val maxCreateTime = groupedList.mapNotNull { (_, statusList) ->
            statusList.firstOrNull { it.nextStatusId.isNullOrEmpty() }?.createTimestamp
        }.maxOrNull() ?: 0L
        return groupedList.flatMap { (_, statusList) ->
            statusList.filter { it.createTimestamp >= maxCreateTime }
        }.sortedByDescending { it.createTimestamp }
    }

    private suspend fun getStatusBeforeSinceId(
        feedsConfig: FeedsConfig,
        sinceId: String,
        limit: Int,
    ): List<StatusContentEntity> {
        val statusEntity = statusContentRepo.query(sinceId) ?: return emptyList()
        return statusContentRepo.queryBefore(
            sourceUriList = feedsConfig.sourceUriList,
            createTimestamp = statusEntity.createTimestamp,
            limit = limit,
        )
    }

    private suspend fun getStatus(feedsConfig: FeedsConfig, limit: Int): List<StatusContentEntity> {
        return statusContentRepo.query(feedsConfig.sourceUriList, limit)
    }
}
