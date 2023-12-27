package com.zhangke.utopia.activitypub.app.internal.usecase.platform

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class GetActivityPubPlatformUseCase @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
) {

    suspend operator fun invoke(baseUrl: FormalBaseUrl): Result<BlogPlatform> {
        return platformRepo.getPlatform(baseUrl)
    }
}
