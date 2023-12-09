package com.zhangke.utopia.common.feeds.repo

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedsRepo @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
    private val statusProvider: StatusProvider,
) {

    suspend fun fetchStatusByFeedsConfig(
        feedsConfig: FeedsConfig,
        limit: Int = 30,
    ): Result<Unit> {
        val statusResolver = statusProvider.statusResolver
        val resultList = coroutineScope {
            feedsConfig.sourceUriList.map {
                async {
                    it to statusResolver.getStatusList(it, limit)
                }
            }.awaitAll()
        }
        resultList.forEach { (uri, result) ->
            val list = result.getOrNull()
            if (!list.isNullOrEmpty()) {
                statusContentRepo.insert(uri, list)
            }
        }
        val hasSuccess = resultList.any { it.second.isSuccess }
        val exception = resultList.mapFirstOrNull { it.second.exceptionOrNull() }
        return if (hasSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(exception ?: IllegalStateException("fetch failed!"))
        }
    }

    fun getStatusFlowByFeedsConfig(feedsConfig: FeedsConfig): Flow<List<Status>> {
        return statusContentRepo.queryBySourceUriList(feedsConfig.sourceUriList)
    }
}
