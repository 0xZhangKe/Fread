package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.uri.FormalUri
import io.github.charlietap.leftright.LeftRight
import me.tatarka.inject.annotations.Inject

/**
 * 用于解除 [ActivityPubClientManager] 以及
 * [com.zhangke.fread.activitypub.app.ActivityPubAccountManager] 之间的依赖关系。
 */
@ApplicationScope
class LoggedAccountProvider @Inject constructor() {

    // About LeftRight: https://github.com/CharlieTap/cachemap
    private val accountSet = LeftRight<MutableSet<ActivityPubLoggedAccount>>(::mutableSetOf)

    fun updateAccounts(accountList: List<ActivityPubLoggedAccount>) {
        accountSet.mutate { set ->
            set.clear()
            set.addAll(accountList)
        }
    }

    fun getAccount(userUri: FormalUri): ActivityPubLoggedAccount? {
        return accountSet.read { set ->
            set.find { it.uri == userUri }
        }
    }

    fun getAccount(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount? {
        val accountList = accountSet.read { set ->
            set.filter { it.baseUrl.equalsDomain(baseUrl) }
        }
        return accountList.firstOrNull()
    }

    fun getAccount(locator: PlatformLocator): ActivityPubLoggedAccount? {
        if (locator.accountUri != null) {
            return getAccount(locator.accountUri!!)
        }
        return getAccount(locator.baseUrl)
    }

    fun getAllAccounts(): List<ActivityPubLoggedAccount> {
        return accountSet.read { set ->
            set.toList()
        }
    }

    fun removeAccount(uri: FormalUri) {
        accountSet.mutate { set ->
            set.find { it.uri == uri }?.let { set.remove(it) }
        }
    }
}
