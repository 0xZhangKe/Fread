package com.zhangke.utopia.activitypubapp.client

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserRepo
import com.zhangke.utopia.status.auth.StatusProviderAuthorizer
import javax.inject.Inject

private val clientCache = mutableMapOf<String, ActivityPubClient>()

class ObtainActivityPubClientUseCase @Inject constructor(
    private val userRepo: ActivityPubUserRepo,
    private val createActivityPubClientUseCase: CreateActivityPubClientUseCase,
) {

    suspend operator fun invoke(host: String): ActivityPubClient {
        clientCache[host]?.let { return it }
        return createActivityPubClientUseCase(
            host = host,
            tokenProvider = {
                userRepo.getCurrentUser()?.token
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
}
