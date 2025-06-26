package com.zhangke.fread.activitypub.app.internal.repo.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.source.UserSourceTransformer
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

class UserRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val userSourceTransformer: UserSourceTransformer,
) {

    suspend fun getUserSource(
        locator: PlatformLocator,
        userUriInsights: UserUriInsights,
    ): Result<StatusSource> {
        val userIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, locator)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(locator)
            .accountRepo
            .getAccount(userId)
            .map {
                userSourceTransformer.createByUserEntity(it)
            }
    }

    suspend fun lookupUserSource(
        locator: PlatformLocator,
        acct: String,
    ): Result<StatusSource?> {
        return clientManager.getClient(locator).accountRepo
            .lookup(acct)
            .onSuccess {
                if (it != null) {
                    val webFinger = WebFinger.create(it.acct)
                    if (webFinger != null) {
                        webFingerBaseUrlToUserIdRepo.insert(webFinger, locator, it.id)
                    }
                }
            }
            .map { it?.let { userSourceTransformer.createByUserEntity(it) } }
    }
}
