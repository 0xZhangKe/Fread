package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.baseurl.ChooseBaseUrlUseCase
import javax.inject.Inject

class IsUserFirstStatusUseCase @Inject constructor(
    private val chooseBaseUrlUseCase: ChooseBaseUrlUseCase,
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
) {

    suspend operator fun invoke(
        userUriInsights: UserUriInsights,
        statusId: String,
    ): Result<Boolean> {
        val baseUrl = chooseBaseUrlUseCase(userUriInsights.uri)
        val userIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, baseUrl)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(baseUrl).accountRepo
            .getStatuses(
                id = userId,
                limit = 1,
                maxId = statusId,
            ).map { it.isEmpty() }
    }
}
