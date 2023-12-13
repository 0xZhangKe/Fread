package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetStatusUseCase @Inject internal constructor(
    private val getStatusFromLocalUseCase: GetStatusFromLocalUseCase,
    private val getStatusFromServerUseCase: GetStatusFromServerUseCase,
    private val statusContentEntityAdapter: StatusContentEntityAdapter,
) {

    suspend operator fun invoke(
        feedsConfig: FeedsConfig,
        sinceId: String?,
        limit: Int = 50,
    ): Result<List<Status>> {
        val statusList = getStatusFromLocalUseCase(feedsConfig, sinceId, limit)
        if (statusList.isNotEmpty()) {
            return Result.success(statusList.map(statusContentEntityAdapter::toStatus))
        }
        return getStatusFromServerUseCase(feedsConfig, sinceId, limit)
    }
}
