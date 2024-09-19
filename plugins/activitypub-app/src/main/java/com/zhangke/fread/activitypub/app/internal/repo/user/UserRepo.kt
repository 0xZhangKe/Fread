package com.zhangke.fread.activitypub.app.internal.repo.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.source.UserSourceTransformer
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

class UserRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val userSourceTransformer: UserSourceTransformer,
) {

    suspend fun getUserSource(
        role: IdentityRole,
        userUriInsights: UserUriInsights,
    ): Result<StatusSource> {
        val userIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, role)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(role)
            .accountRepo
            .getAccount(userId)
            .map(userSourceTransformer::createByUserEntity)
    }

    suspend fun lookupUserSource(
        role: IdentityRole,
        acct: String,
    ): Result<StatusSource?> {
        return clientManager.getClient(role).accountRepo
            .lookup(acct)
            .onSuccess {
                if (it != null) {
                    val webFinger = WebFinger.create(it.acct)
                    if (webFinger != null) {
                        webFingerBaseUrlToUserIdRepo.insert(webFinger, role, it.id)
                    }
                }
            }
            .map { it?.let(userSourceTransformer::createByUserEntity) }
    }
}
