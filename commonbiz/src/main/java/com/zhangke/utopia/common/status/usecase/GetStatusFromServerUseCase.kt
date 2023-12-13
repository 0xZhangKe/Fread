package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

internal class GetStatusFromServerUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentRepo: StatusContentRepo,
    private val saveStatusListToLocal: SaveStatusListToLocalUseCase,
) {

    private val statusResolver get() = statusProvider.statusResolver

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int,
    ): Result<List<Status>> {
        val sinceIdOfSourceUri = if (!sinceId.isNullOrEmpty()) {
            statusContentRepo.querySourceById(sinceId)?.sourceUri
        } else {
            null
        }
        //如果 sinceId  不为空，那么该 source 的从 sinceId 直接加载即可。
        // 但是得考虑其他 source 的怎么办。server 没有 startTime 之类的参数可以用。

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
        return resultPairList.mapNotNull { it.second.getOrNull() }.flatten()
            .let { Result.success(it) }
    }

    private suspend fun getStatusFromServer(
        sourceUri: StatusProviderUri,
        sinceId: String?,
        limit: Int,
    ): Result<List<Status>> {
        val statusListResult = statusResolver.getStatusList(
            uri = sourceUri,
            limit = limit,
            sinceId = sinceId,
        )
        val statusList = statusListResult.getOrNull() ?: return statusListResult
        if (statusList.isEmpty()) return statusListResult
        val nextIdOfLatest = if (statusList.size < limit) {
            val isFirstStatus = statusResolver.checkIsFirstStatus(sourceUri, statusList.last().id).getOrNull() == true
            if (isFirstStatus) StatusContentRepo.STATUS_END_MAGIC_NUMBER else null
        } else {
            null
        }
        saveStatusListToLocal(
            statusSourceUri = sourceUri,
            statusList = statusList,
            previousId = sinceId,
            nextIdOfLatest = nextIdOfLatest,
        )
        return statusListResult
    }
}
