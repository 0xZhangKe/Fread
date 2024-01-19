package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class IsUserFirstStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
) {

    suspend operator fun invoke(
        status: Status,
    ): Result<Boolean> {
        val baseUrl = status.platform.baseUrl
        val userIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(status.intrinsicBlog.author.webFinger, baseUrl)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(baseUrl).accountRepo
            .getStatuses(
                id = userId,
                limit = 1,
                maxId = status.id,
            ).map { it.isEmpty() }
    }
}
