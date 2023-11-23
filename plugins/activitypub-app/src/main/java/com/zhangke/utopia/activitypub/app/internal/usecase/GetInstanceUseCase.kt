package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import javax.inject.Inject

internal class GetInstanceUseCase @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
) {

    suspend operator fun invoke(host: String): Result<ActivityPubInstanceEntity> {
        return obtainActivityPubClient(host).instanceRepo.getInstanceInformation()
    }
}
