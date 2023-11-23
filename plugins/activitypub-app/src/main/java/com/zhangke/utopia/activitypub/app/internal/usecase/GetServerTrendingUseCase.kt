package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import javax.inject.Inject

class GetServerTrendingUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) {

    suspend operator fun invoke(
        host: String,
        limit: Int,
        offset: Int,
    ): Result<List<ActivityPubStatusEntity>> {
        return obtainActivityPubClientUseCase(host).instanceRepo.getTrendsStatuses(
            limit = limit,
            offset = offset,
        )
    }
}
