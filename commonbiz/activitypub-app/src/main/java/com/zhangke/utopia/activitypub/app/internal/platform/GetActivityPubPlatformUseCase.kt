package com.zhangke.utopia.activitypub.app.internal.platform

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.status.platform.UtopiaPlatform
import javax.inject.Inject

class GetActivityPubPlatformUseCase @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
) {

    suspend operator fun invoke(host: String): Result<UtopiaPlatform> {
        return obtainActivityPubClient(host).instanceRepo
            .getInstanceInformation().map {
                it.let(activityPubInstanceAdapter::toPlatform)
            }
    }
}
