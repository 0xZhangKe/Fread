package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserRepo

internal fun newActivityPubClient(app: ActivityPubApplication): ActivityPubClient {
    return ActivityPubClient(
        application = app,
        retrofit = newRetrofit(buildBaseUrl(app.domain)),
        gson = globalGson,
        tokenProvider = {
            ActivityPubUserRepo.getCurrentUser()?.token
        },
        onAuthorizeFailed = { url, client ->
            ActivityPubOAuthor.openOauthTipPage(url, client, true)
        }
    )
}

internal fun newActivityPubClient(domain: String): ActivityPubClient {
    return newActivityPubClient(newActivityPubApplication(domain))
}

private fun buildBaseUrl(domain: String): String {
    return "https://$domain"
}