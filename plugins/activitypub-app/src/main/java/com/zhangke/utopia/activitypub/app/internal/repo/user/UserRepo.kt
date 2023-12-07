package com.zhangke.utopia.activitypub.app.internal.repo.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriData
import com.zhangke.utopia.activitypub.app.internal.source.UserSourceTransformer
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val userSourceTransformer: UserSourceTransformer,
) {

    suspend fun getUserSource(userUriData: UserUriData): Result<StatusSource> {
        return clientManager.getClient(userUriData.webFinger.host.toBaseUrl())
            .accountRepo
            .getAccount(userUriData.userId)
            .map(userSourceTransformer::createByUserEntity)
    }

    suspend fun lookupUserSource(webFinger: WebFinger): Result<StatusSource?> {
        return clientManager.getClient(webFinger.host.toBaseUrl()).accountRepo
            .lookup(webFinger.toString())
            .map {
                it?.let(userSourceTransformer::createByUserEntity)
            }
    }
}
