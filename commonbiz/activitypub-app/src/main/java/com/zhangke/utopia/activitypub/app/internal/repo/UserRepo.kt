package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.source.user.UserSource
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
) {

    suspend fun getUserSource(userUri: ActivityPubUserUri): Result<UserSource> {
        val client = obtainActivityPubClient(userUri.finger.host)
        return client.accountRepo
            .getAccount(userUri.userId)
            .map(accountEntityAdapter::toUserSource)
    }

    suspend fun lookupUserSource(webFinger: WebFinger): Result<UserSource?> {
        val client = obtainActivityPubClient(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map {
                it?.let(accountEntityAdapter::toUserSource)
            }
    }
}
