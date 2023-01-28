package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserRepo

private const val REDIRECT_URI = "utopia://oauth.utopia"

internal fun newActivityPubClient(app: ActivityPubApplication): ActivityPubClient {
    return ActivityPubClient(
        application = app,
        retrofit = newRetrofit(buildBaseUrl(app.host)),
        gson = globalGson,
        tokenProvider = {
            ActivityPubUserRepo.getCurrentUser()?.token
        },
        onAuthorizeFailed = { url, client ->
            ActivityPubOAuthor.openOauthTipPage(url, client, true)
        }
    )
}

private val clientCache = mutableMapOf<String, ActivityPubClient>()

internal fun newActivityPubClient(host: String): ActivityPubClient {
    return newActivityPubClient(newActivityPubApplication(host)).apply {
        clientCache[host] = this
    }
}

internal fun obtainActivityPubClient(host: String): ActivityPubClient {
    return clientCache[host] ?: newActivityPubClient(host)
}

private fun buildBaseUrl(host: String): String {
    return "https://$host"
}