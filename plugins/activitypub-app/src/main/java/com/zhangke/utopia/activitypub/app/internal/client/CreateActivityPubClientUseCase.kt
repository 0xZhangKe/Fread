package com.zhangke.utopia.activitypub.app.internal.client

import com.google.gson.Gson
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import retrofit2.Retrofit
import javax.inject.Inject

class CreateActivityPubClientUseCase @Inject constructor(
    private val obtainActivityPubApplicationUseCase: ObtainActivityPubApplicationUseCase,
) {

    suspend operator fun invoke(
        host: String,
        gson: Gson = globalGson,
        tokenProvider: suspend () -> ActivityPubTokenEntity?,
        onAuthorizeFailed: suspend () -> Unit,
    ): ActivityPubClient {
        return ActivityPubClient(
            baseUrl = "https:$host",
            httpClient = GlobalOkHttpClient.client,
            gson = gson,
            tokenProvider = tokenProvider,
            onAuthorizeFailed = onAuthorizeFailed,
        )
    }
}
