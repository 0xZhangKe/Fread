package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.toDomain
import javax.inject.Inject

class LaunchActivityPubAuthUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val author: ActivityPubOAuthor,
) {

    suspend fun launch(baseUrl: String): Result<Boolean> {
        if (baseUrl.isEmpty()) {
            return Result.failure(IllegalArgumentException("Illegal platform:$baseUrl"))
        }
        val client = obtainActivityPubClientUseCase(baseUrl.toDomain())
        return Result.success(author.startOauth(client.buildOAuthUrl(), client))
    }
}
