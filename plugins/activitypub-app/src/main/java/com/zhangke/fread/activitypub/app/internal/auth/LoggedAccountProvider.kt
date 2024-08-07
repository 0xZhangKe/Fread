package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.framework.collections.container
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.uri.FormalUri
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用于解除 [ActivityPubClientManager] 以及
 * [com.zhangke.fread.activitypub.app.ActivityPubAccountManager] 之间的依赖关系。
 */
@Singleton
class LoggedAccountProvider @Inject constructor() {

    private val accountSet = CopyOnWriteArraySet<ActivityPubLoggedAccount>()

    fun updateAccounts(accountList: List<ActivityPubLoggedAccount>){
        accountSet.clear()
        accountSet.addAll(accountList)
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
