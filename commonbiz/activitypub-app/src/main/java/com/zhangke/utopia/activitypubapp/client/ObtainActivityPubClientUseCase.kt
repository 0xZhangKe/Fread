package com.zhangke.utopia.activitypubapp.client

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.activitypubapp.auth.ActivityPubOAuthor
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubAccountRepo
import javax.inject.Inject

private val clientCache = mutableMapOf<String, ActivityPubClient>()

class ObtainActivityPubClientUseCase @Inject constructor(
    private val userRepo: ActivityPubAccountRepo,
    private val createActivityPubClientUseCase: CreateActivityPubClientUseCase,
    private val author: ActivityPubOAuthor,
) {

    suspend operator fun invoke(host: String): ActivityPubClient {
        clientCache[host]?.let { return it }
        return createActivityPubClientUseCase(
            host = host,
            tokenProvider = {
                userRepo.getCurrentUser()?.token
            },
            onAuthorizeFailed = { url, client ->
                author.startOauth(url, client)
            }
        ).apply {
            clientCache[host] = this
        }
    }
}
