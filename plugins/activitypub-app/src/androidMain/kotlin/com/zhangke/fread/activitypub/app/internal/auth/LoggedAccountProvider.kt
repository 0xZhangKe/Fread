package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 用于解除 [ActivityPubClientManager] 以及
 * [com.zhangke.fread.activitypub.app.ActivityPubAccountManager] 之间的依赖关系。
 */
@ApplicationScope
class LoggedAccountProvider @Inject constructor() {

    private val accountSet = CopyOnWriteArraySet<ActivityPubLoggedAccount>()

    fun updateAccounts(accountList: List<ActivityPubLoggedAccount>) {
        accountSet.clear()
        accountSet.addAll(accountList)
    }

    fun getAccount(userUri: FormalUri): ActivityPubLoggedAccount? {
        return accountSet.find { it.uri == userUri }
    }

    fun getAccount(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount? {
        return accountSet.find { it.platform.baseUrl.equalsDomain(baseUrl) }
    }

    fun removeAccount(uri: FormalUri) {
        accountSet.find { it.uri == uri }?.let { accountSet.remove(it) }
    }
}
