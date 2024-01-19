package com.zhangke.utopia.activitypub.app.internal.repo.user

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.source.UserSourceTransformer
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val userSourceTransformer: UserSourceTransformer,
) {

    suspend fun getUserSource(
        baseUrl: FormalBaseUrl,
        userUriInsights: UserUriInsights,
    ): Result<StatusSource> {
        val userIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, baseUrl)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(baseUrl)
            .accountRepo
            .getAccount(userId)
            .map(userSourceTransformer::createByUserEntity)
    }

    suspend fun lookupUserSource(
        baseUrl: FormalBaseUrl,
        acct: String,
    ): Result<StatusSource?> {
        return clientManager.getClient(baseUrl).accountRepo
            .lookup(acct)
            .onSuccess {
                if (it != null) {
                    val webFinger = WebFinger.create(it.acct)
                    if (webFinger != null) {
                        webFingerBaseUrlToUserIdRepo.insert(webFinger, baseUrl, it.id)
                    }
                }
            }
            .map { it?.let(userSourceTransformer::createByUserEntity) }
    }
}
