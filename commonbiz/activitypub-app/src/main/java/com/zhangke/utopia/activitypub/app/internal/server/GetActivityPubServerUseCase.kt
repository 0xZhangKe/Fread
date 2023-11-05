package com.zhangke.utopia.activitypub.app.internal.server

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
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
