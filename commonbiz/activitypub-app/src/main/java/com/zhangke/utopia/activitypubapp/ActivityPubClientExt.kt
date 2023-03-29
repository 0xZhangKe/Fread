package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserRepo
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.auth.StatusProviderAuthorizer

private const val REDIRECT_URI = "utopia://oauth.utopia"

private val clientCache = mutableMapOf<String, ActivityPubClient>()

internal val currentActivityPubClient: ActivityPubClient?
    get() {
        val host = ActivityPubUserRepo.getCurrentUser()?.domain ?: return null
        return obtainActivityPubClient(host)
    }

internal fun obtainActivityPubClient(host: String): ActivityPubClient {
    return clientCache[host] ?: newActivityPubClient(host)
}

private fun newActivityPubClient(host: String): ActivityPubClient {
    val application = obtainActivityPubApplication(host)
    return ActivityPubClient(
        application = application,
        retrofit = newRetrofit(ActivityPubUrl.create(host)!!.completenessUrl),
        gson = globalGson,
        redirectUrl = REDIRECT_URI,
        tokenProvider = {
            ActivityPubUserRepo.getCurrentUser()?.token
        },
        onAuthorizeFailed = { url, client ->
            StatusProviderAuthorizer.onAuthenticationFailure {
                ActivityPubOAuthor.startOauth(url, client)
            }
        }
    ).apply {
        clientCache[host] = this
    }
}