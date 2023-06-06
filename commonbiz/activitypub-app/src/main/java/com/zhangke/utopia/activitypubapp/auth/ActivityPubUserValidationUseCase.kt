package com.zhangke.utopia.activitypubapp.auth

import com.zhangke.utopia.activitypubapp.client.CreateActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserEntity
import javax.inject.Inject

class ActivityPubUserValidationUseCase @Inject constructor(
    private val createActivityPubClientUseCase: CreateActivityPubClientUseCase,
) {

    suspend operator fun invoke(userEntity: ActivityPubUserEntity): Result<Boolean> {
        val host = userEntity.host
        val client = createActivityPubClientUseCase(
            host = host,
            tokenProvider = {
                userEntity.token
            },
            onAuthorizeFailed = { _, _ ->

            }
        )
        val success = client.timelinesRepo.homeTimeline(
            maxId = "",
            minId = "",
            sinceId = "",
            limit = 5,
        ).isSuccess
        return Result.success(success)
    }
}
