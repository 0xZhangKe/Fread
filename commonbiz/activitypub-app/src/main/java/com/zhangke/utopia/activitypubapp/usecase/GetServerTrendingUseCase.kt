package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.activitypub.entry.ActivityPubStatusEntity
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import javax.inject.Inject

class GetServerTrendingUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) {

    suspend operator fun invoke(host: String): Result<List<ActivityPubStatusEntity>> {
        return obtainActivityPubClientUseCase(host).instanceRepo.getTrendsStatuses(
            limit = 10,
            offset = 0,
        )
    }
}
