package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import me.tatarka.inject.annotations.Inject

class GetUserStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        userInsights: UserUriInsights,
        limit: Int,
        maxId: String?,
    ): Result<List<StatusUiState>> {
        val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(userInsights.webFinger, role)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) return Result.failure(platformResult.exceptionOrNull()!!)
        val platform = platformResult.getOrThrow()
        val account = loggedAccountProvider.getAccount(role)
        return clientManager.getClient(role)
            .accountRepo.getStatuses(
                id = userId,
                limit = limit,
                maxId = maxId,
            ).map { list ->
                list.map {
                    activityPubStatusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        role = role,
                        loggedAccount = account,
                    )
                }
            }
    }
}
