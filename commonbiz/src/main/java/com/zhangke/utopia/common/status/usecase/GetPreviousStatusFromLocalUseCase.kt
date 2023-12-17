package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import javax.inject.Inject

/**
 * 获取更早的 status 列表。
 * 如果传入了 sinceId，那就获取这个 sinceId 之前的 status。
 */
internal class GetPreviousStatusFromLocalUseCase @Inject internal constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int,
    ): List<StatusContentEntity> {
        val list = if (sinceId.isNullOrEmpty()) {
            getStatus(feedsConfig)
        } else {
            val statusList = getStatusBeforeSinceId(feedsConfig, sinceId)
            statusList.ifEmpty {
                getStatus(feedsConfig)
            }
        }
        return list.groupBy { it.sourceUri }
            .filter { it.value.isNotEmpty() }
            .flatMap { (_, statusList) ->
                val breakIndex = statusList.indexOfFirst { it.nextStatusId.isNullOrEmpty() }
                if (breakIndex >= 0) {
                    statusList.subList(0, breakIndex + 1)
                } else {
                    statusList
                }
            }
            .filter { it.id != sinceId }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
    }

    private suspend fun getStatusBeforeSinceId(
        feedsConfig: FeedsConfig,
        sinceId: String,
    ): List<StatusContentEntity> {
        val statusEntity = statusContentRepo.query(sinceId) ?: return emptyList()
        return statusContentRepo.queryBefore(
            sourceUriList = feedsConfig.sourceUriList,
            createTimestamp = statusEntity.createTimestamp,
        )
    }

    private suspend fun getStatus(feedsConfig: FeedsConfig): List<StatusContentEntity> {
        return statusContentRepo.query(feedsConfig.sourceUriList)
    }
}
