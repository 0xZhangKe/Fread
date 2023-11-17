package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.utopia.activitypub.app.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class LaunchActivityPubAuthUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val author: ActivityPubOAuthor,
) {

    fun applicable(platform: BlogPlatform): Boolean {
        return platform.protocol == ACTIVITY_PUB_PROTOCOL
    }

    suspend fun launch(platform: BlogPlatform): Result<Boolean> {
        if (platform.baseUrl.isEmpty()) {
            return Result.failure(IllegalArgumentException("Illegal platform:$platform"))
        }
        val client = obtainActivityPubClientUseCase(platform.baseUrl)
        return Result.success(author.startOauth(client.buildOAuthUrl(), client))
    }
}
