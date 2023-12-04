package com.zhangke.utopia.activitypub.app.internal.repo.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserSource
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
) {

    suspend fun getUserSource(userUri: ActivityPubUserUri): Result<UserSource> {
        return clientManager.getClient(userUri.finger.host.toBaseUrl())
            .accountRepo
            .getAccount(userUri.userId)
            .map(accountEntityAdapter::toUserSource)
    }

    suspend fun lookupUserSource(webFinger: WebFinger): Result<UserSource?> {
        return clientManager.getClient(webFinger.host.toBaseUrl()).accountRepo
            .lookup(webFinger.toString())
            .map {
                it?.let(accountEntityAdapter::toUserSource)
            }
    }
}
