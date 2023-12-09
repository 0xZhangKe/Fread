package com.zhangke.utopia.activitypub.app.internal.usecase.client

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import javax.inject.Inject

class GetClientUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val accountManager: ActivityPubAccountManager,
) {

    companion object {

        private const val DEFAULT_BASE_URL = "https://mastodon.social"
    }

    suspend operator fun invoke(baseUrl: String? = null): ActivityPubClient {
        var decideBaseUrl = baseUrl
        if (decideBaseUrl.isNullOrEmpty()) {
            decideBaseUrl = accountManager.getActiveAccount()?.platform?.baseUrl
        }
        if (decideBaseUrl.isNullOrEmpty()) {
            decideBaseUrl = accountManager.getAllLoggedAccount().firstOrNull()?.platform?.baseUrl
        }
        if (decideBaseUrl.isNullOrEmpty()) {
            decideBaseUrl = DEFAULT_BASE_URL
        }
        return clientManager.getClient(decideBaseUrl)
    }
}
