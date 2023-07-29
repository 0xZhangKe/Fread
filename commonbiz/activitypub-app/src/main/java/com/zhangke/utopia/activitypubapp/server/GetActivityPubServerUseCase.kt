package com.zhangke.utopia.activitypubapp.server

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.status.server.StatusProviderServer
import javax.inject.Inject

class GetActivityPubServerUseCase @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
) {

    suspend operator fun invoke(host: String): Result<StatusProviderServer> {
        return obtainActivityPubClient(host).instanceRepo
            .getInstanceInformation().map {
                it.let(activityPubInstanceAdapter::adapt)
            }
    }
}
