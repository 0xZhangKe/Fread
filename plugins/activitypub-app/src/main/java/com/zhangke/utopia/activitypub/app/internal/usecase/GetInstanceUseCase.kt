package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import javax.inject.Inject

internal class GetInstanceUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(baseUrl: String): Result<ActivityPubInstanceEntity> {
        return clientManager.getClient(baseUrl).instanceRepo.getInstanceInformation()
    }
}
