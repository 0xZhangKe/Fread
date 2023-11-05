package com.zhangke.utopia.activitypub.app.internal.user

import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubUser
import com.zhangke.utopia.activitypub.app.internal.uri.user.ActivityPubUserUri
import com.zhangke.framework.utils.WebFinger
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val userAdapter: ActivityPubUserAdapter,
) {

    suspend fun getUserSource(userUri: ActivityPubUserUri): Result<ActivityPubUser> {
        val client = obtainActivityPubClient(userUri.finger.host)
        return client.accountRepo
            .getAccount(userUri.userId)
            .map { userAdapter.adapt(it) }
    }

    suspend fun lookup(webFinger: WebFinger): Result<ActivityPubUser?> {
        val client = obtainActivityPubClient(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { it?.let(userAdapter::adapt) }
    }
}
