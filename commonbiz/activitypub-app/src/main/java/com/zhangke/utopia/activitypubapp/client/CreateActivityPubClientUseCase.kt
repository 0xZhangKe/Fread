package com.zhangke.utopia.activitypubapp.client

import com.google.gson.Gson
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
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
        tokenProvider: suspend () -> ActivityPubToken?,
        onAuthorizeFailed: (url: String, client: ActivityPubClient) -> Unit,
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
