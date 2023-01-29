package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserRepo

private const val REDIRECT_URI = "utopia://oauth.utopia"

private val clientCache = mutableMapOf<String, ActivityPubClient>()

internal fun obtainActivityPubClient(host: String): ActivityPubClient {
    return clientCache[host] ?: newActivityPubClient(host)
}

private fun newActivityPubClient(host: String): ActivityPubClient {
    val application = obtainActivityPubApplication(host)
    return ActivityPubClient(
        application = application,
        retrofit = newRetrofit(buildBaseUrl(application.host)),
        gson = globalGson,
        redirectUrl = REDIRECT_URI,
        tokenProvider = {
            ActivityPubUserRepo.getCurrentUser()?.token
        },
        onAuthorizeFailed = { url, client ->
            ActivityPubOAuthor.openOauthTipPage(url, client, true)
        }
    ).apply {
        clientCache[host] = this
    }
}

private fun buildBaseUrl(host: String): String {
    return "https://$host"
}