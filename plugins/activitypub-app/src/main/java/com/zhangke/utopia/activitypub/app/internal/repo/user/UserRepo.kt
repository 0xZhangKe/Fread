package com.zhangke.utopia.activitypub.app.internal.repo.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.source.UserSourceTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.baseurl.ChooseBaseUrlUseCase
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val chooseBaseUrl: ChooseBaseUrlUseCase,
    private val userSourceTransformer: UserSourceTransformer,
) {

    suspend fun getUserSource(userUriInsights: UserUriInsights): Result<StatusSource> {
        val baseUrl = chooseBaseUrl(userUriInsights.uri)
        val userIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, baseUrl)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(baseUrl)
            .accountRepo
            .getAccount(userId)
            .map(userSourceTransformer::createByUserEntity)
    }

    suspend fun lookupUserSource(webFinger: WebFinger): Result<StatusSource?> {
        val baseUrl = chooseBaseUrl()
        return clientManager.getClient(baseUrl).accountRepo
            .lookup(webFinger.toString())
            .map {
                if (it != null) {
                    webFingerBaseUrlToUserIdRepo.insert(webFinger, baseUrl, it.id)
                }
                it?.let(userSourceTransformer::createByUserEntity)
            }
    }
}
