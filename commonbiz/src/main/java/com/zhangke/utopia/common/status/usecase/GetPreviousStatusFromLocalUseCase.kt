package com.zhangke.utopia.common.status.usecase

import android.util.Log
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
        maxId: String?,
        limit: Int,
    ): List<StatusContentEntity> {
        Log.d("U_TEST", "GetPreviousStatusFromLocal feeds is ${feedsConfig.name}, sinceId is ${maxId}")
        val list = if (maxId.isNullOrEmpty()) {
            getStatus(feedsConfig)
        } else {
            val statusList = getStatusBeforeSinceId(feedsConfig, maxId)
            Log.d("U_TEST", "getStatusBeforeSinceId result size is ${statusList.size}")
            statusList.ifEmpty {
                getStatus(feedsConfig)
            }
        }
        Log.d("U_TEST", "getStatusBeforeSinceId local result status size is ${list.size}")
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
            .filter { it.id != maxId }
            .sortedByDescending { it.createTimestamp }
            .take(limit)
    }

    private suspend fun getStatusBeforeSinceId(
        feedsConfig: FeedsConfig,
        maxId: String,
    ): List<StatusContentEntity> {
        val statusEntity = statusContentRepo.query(maxId) ?: return emptyList()
        return statusContentRepo.queryBefore(
            sourceUriList = feedsConfig.sourceUriList,
            createTimestamp = statusEntity.createTimestamp,
        )
    }

    private suspend fun getStatus(feedsConfig: FeedsConfig): List<StatusContentEntity> {
        return statusContentRepo.query(feedsConfig.sourceUriList)
    }
}
