package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityPubClientManager @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
) {

    private val clientList = mutableListOf<ActivityPubClient>()

    fun getClient(baseUrl: FormalBaseUrl): ActivityPubClient {
        clientList.firstOrNull { it.baseUrl == baseUrl.toString() }?.let { return it }
        val client = createClient(baseUrl)
        clientList += client
        return client
    }

    private fun createClient(baseUrl: FormalBaseUrl): ActivityPubClient {
        return ActivityPubClient(
            baseUrl = "${baseUrl}/",
            httpClient = GlobalOkHttpClient.client,
            gson = globalGson,
            tokenProvider = {
                accountManager.getAllLoggedAccount().firstOrNull { it.baseUrl == baseUrl }?.token
            },
            onAuthorizeFailed = {

            },
        )
    }
}
