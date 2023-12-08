package com.zhangke.utopia.common.feeds.repo

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class FeedsRepo @Inject constructor(
    private val statusContentRepo: StatusContentRepo,
) {

    suspend fun getStatusByFeedsConfig(feedsConfig: FeedsConfig): List<Status> {
        return statusContentRepo.queryBySourceUriList(feedsConfig.sourceUriList)
    }
}
