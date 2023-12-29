package com.zhangke.utopia.common.feeds.repo

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.usecase.newer.GetNewerStatusUseCase
import com.zhangke.utopia.common.status.usecase.previous.GetPreviousStatusUseCase
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class FeedsRepo @Inject internal constructor(
    private val getPreviousStatusUseCase: GetPreviousStatusUseCase,
    private val getNewerStatusUseCase: GetNewerStatusUseCase,
    private val statusContentRepo: StatusContentRepo,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    companion object {

        private const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getPreviousStatus(
        feedsConfig: FeedsConfig,
        limit: Int = DEFAULT_PAGE_SIZE,
        maxId: String? = null,
    ): Result<List<Status>> {
        return getPreviousStatusUseCase(
            sourceUriList = feedsConfig.sourceUriList,
            limit = limit,
            maxId = maxId,
        )
    }

    suspend fun getNewerStatus(
        feedsConfig: FeedsConfig,
        minStatusId: String,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): Result<List<Status>> {
        return getNewerStatusUseCase(
            sourceUriList = feedsConfig.sourceUriList,
            limit = limit,
            minStatusId = minStatusId,
        )
    }

    suspend fun updateStatus(status: Status) {
        val existStatus = statusContentRepo.queryByPlatformId(status.id) ?: return
        val newStatus = existStatus.copy(status = status)
        statusContentRepo.insert(newStatus)
    }
}
