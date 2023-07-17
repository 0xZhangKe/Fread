package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import javax.inject.Inject

internal class GetInstanceUseCase @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
) {

    suspend operator fun invoke(host: String): Result<ActivityPubInstanceEntity> {
        return obtainActivityPubClient(host).instanceRepo.getInstanceInformation()
    }
}
