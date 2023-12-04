package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityPubClientManager @Inject constructor(
    private val loggedAccountRepo: ActivityPubLoggedAccountRepo,
) {

    private val clientList = mutableListOf<ActivityPubClient>()

    fun getClient(baseUrl: String): ActivityPubClient {
        clientList.firstOrNull { it.baseUrl == baseUrl }?.let { return it }
        val client = createClient(baseUrl)
        clientList += client
        return client
    }

    private fun createClient(baseUrl: String): ActivityPubClient {
        return ActivityPubClient(
            baseUrl = baseUrl,
            httpClient = GlobalOkHttpClient.client,
            gson = globalGson,
            tokenProvider = {
                loggedAccountRepo.getUserByBaseUrl(baseUrl)?.token
            },
            onAuthorizeFailed = {

            },
        )
    }
}
