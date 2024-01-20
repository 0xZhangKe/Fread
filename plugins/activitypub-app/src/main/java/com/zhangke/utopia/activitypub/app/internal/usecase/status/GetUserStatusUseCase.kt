package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class GetUserStatusUseCase @Inject constructor(
    private val baseUrlManager: BaseUrlManager,
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val getStatusSupportInteraction: GetStatusInteractionUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
) {

    suspend operator fun invoke(
        userInsights: UserUriInsights,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>> {
        val baseUrl = baseUrlManager.decideBaseUrl(userInsights.baseUrl)
        val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(userInsights.webFinger, baseUrl)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        val platformResult = platformRepo.getPlatform(baseUrl)
        if (platformResult.isFailure) return Result.failure(platformResult.exceptionOrNull()!!)
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(baseUrl)
            .accountRepo.getStatuses(
                id = userId,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            ).map { list ->
                list.map {
                    val supportActions = getStatusSupportInteraction(it, platform)
                    activityPubStatusAdapter.toStatus(it, platform, supportActions)
                }
            }
    }
}
