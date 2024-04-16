package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用于解除 [ActivityPubClientManager] 以及
 * [com.zhangke.utopia.activitypub.app.ActivityPubAccountManager] 之间的依赖关系。
 */
@Singleton
class LoggedAccountProvider @Inject constructor() {

    private val accountSet = mutableSetOf<ActivityPubLoggedAccount>()

    fun addAccount(account: ActivityPubLoggedAccount) {
        accountSet.add(account)
    }

    fun getAccount(userUri: FormalUri): ActivityPubLoggedAccount? {
        return accountSet.find { it.uri == userUri }
    }

    fun getAccount(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount? {
        return accountSet.find { it.platform.baseUrl == baseUrl }
    }

    fun removeAccount(uri: FormalUri) {
        accountSet.find { it.uri == uri }?.let { accountSet.remove(it) }
    }
}
