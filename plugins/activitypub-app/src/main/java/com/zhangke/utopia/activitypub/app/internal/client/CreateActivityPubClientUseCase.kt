package com.zhangke.utopia.activitypub.app.internal.client

import com.google.gson.Gson
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import retrofit2.Retrofit
import javax.inject.Inject

private const val REDIRECT_URI = "utopia://oauth.utopia"

class CreateActivityPubClientUseCase @Inject constructor(
    private val obtainActivityPubApplicationUseCase: ObtainActivityPubApplicationUseCase,
) {

    suspend operator fun invoke(
        host: String,
        retrofit: Retrofit = newRetrofit(ActivityPubUrl.create(host)!!.completenessUrl),
        gson: Gson = globalGson,
        redirectUrl: String = REDIRECT_URI,
        tokenProvider: suspend () -> ActivityPubTokenEntity?,
        onAuthorizeFailed: suspend (url: String, client: ActivityPubClient) -> Unit,
    ): ActivityPubClient {
        val application = obtainActivityPubApplicationUseCase(host)
        return ActivityPubClient(
            application = application,
            retrofit = retrofit,
            gson = gson,
            redirectUrl = redirectUrl,
            tokenProvider = tokenProvider,
            onAuthorizeFailed = onAuthorizeFailed,
        )
    }
}
