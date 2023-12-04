package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypub.app.internal.account.ActivityPubLoggedAccount
import javax.inject.Inject

class ActivityPubAccountValidationUseCase @Inject constructor() {

    suspend operator fun invoke(userEntity: ActivityPubLoggedAccount): Result<Boolean> {
        val client = ActivityPubClient(
            baseUrl = userEntity.baseUrl,
            httpClient = GlobalOkHttpClient.client,
            gson = globalGson,
            tokenProvider = {
                userEntity.token
            },
            onAuthorizeFailed = {
                // ignore
            },
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
