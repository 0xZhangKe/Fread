package com.zhangke.utopia.activitypub.app.internal.client

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountRepo
import javax.inject.Inject

private val clientCache = mutableMapOf<String, ActivityPubClient>()

class ObtainActivityPubClientUseCase @Inject constructor(
    private val userRepo: ActivityPubLoggedAccountRepo,
    private val createActivityPubClientUseCase: CreateActivityPubClientUseCase,
    private val author: ActivityPubOAuthor,
) {

    suspend operator fun invoke(host: String): ActivityPubClient {
        // TODO Always use single client, maybe use current logged user.
        clientCache[host]?.let { return it }
        return createActivityPubClientUseCase(
            host = host,
            tokenProvider = {
                userRepo.getCurrentAccount()?.token
            },
            onAuthorizeFailed = { url, client ->
                author.startOauth(url, client)
            }
        ).apply {
            clientCache[host] = this
        }
    }
}
