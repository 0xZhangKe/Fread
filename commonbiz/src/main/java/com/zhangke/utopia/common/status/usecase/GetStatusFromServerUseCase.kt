package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetStatusFromServerUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int = 50,
    ): Result<List<Status>> {
        val statusResolver = statusProvider.statusResolver
        val resultPairList = feedsConfig.sourceUriList.map {
            it to statusResolver.getStatusList(
                uri = it,
                limit = limit,
                sinceId = sinceId,
            )
        }
        if (!resultPairList.any { it.second.isSuccess }) {
            val exception = resultPairList.mapFirstOrNull { it.second.exceptionOrNull() }
                ?: IllegalStateException("fetch failed!")
            return Result.failure(exception)
        }
        resultPairList.forEach { (uri, result) ->
            val statusList = result.getOrNull() ?: return@forEach
            saveStatusListToLocal(
                statusSourceUri = uri,
                statusList = statusList,
                previousId = sinceId,
            )
        }
        return resultPairList.mapNotNull { it.second.getOrNull() }.flatten().let {
            Result.success(it)
        }
    }
}