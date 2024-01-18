package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import javax.inject.Inject

class GetServerTrendingUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        limit: Int = StatusConfigurationDefault.config.loadFromServerLimit,
        offset: Int = 0,
    ): Result<List<ActivityPubStatusEntity>> {
        return clientManager.getClient(baseUrl).instanceRepo.getTrendsStatuses(
            limit = limit,
            offset = offset,
        )
    }
}
