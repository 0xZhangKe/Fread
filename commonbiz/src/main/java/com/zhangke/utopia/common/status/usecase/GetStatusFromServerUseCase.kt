package com.zhangke.utopia.common.status.usecase

import android.net.http.UrlRequest.Status
import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.status.StatusProvider
import javax.inject.Inject

class GetStatusFromServerUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
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
            val exception = resultPairList.mapFirstOrNull { it.second.exceptionOrNull() } ?: IllegalStateException("fetch failed!")
            return Result.failure(exception)
        }
        val entityList = resultPairList.mapNotNull {
            val list = it.second.getOrNull()
            if (list.isNullOrEmpty()) {
                return@mapNotNull null
            }
            statusContentEntityAdapter.toEntityList(it.first, list)
        }

    }
}