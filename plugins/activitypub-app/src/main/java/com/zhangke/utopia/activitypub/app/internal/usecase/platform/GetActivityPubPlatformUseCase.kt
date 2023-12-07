package com.zhangke.utopia.activitypub.app.internal.usecase.platform

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class GetActivityPubPlatformUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
) {

    suspend operator fun invoke(baseUrl: String): Result<BlogPlatform> {
        return clientManager.getClient(baseUrl).instanceRepo
            .getInstanceInformation().map {
                it.let(activityPubInstanceAdapter::toPlatform)
            }
    }
}
