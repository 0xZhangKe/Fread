package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.throwInDebug
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
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
        if (accountList.size > 1) {
            throwInDebug("Multiple accounts found for base URL: $baseUrl")
            return null
        }
        return accountList.firstOrNull()
    }

    fun getAccount(role: IdentityRole): ActivityPubLoggedAccount? {
        var account: ActivityPubLoggedAccount? = null
        if (role.accountUri != null) {
            account = getAccount(role.accountUri!!)
        }
        if (account == null && role.baseUrl != null) {
            account = getAccount(role.baseUrl!!)
        }
        return account
    }

    fun removeAccount(uri: FormalUri) {
        accountSet.mutate { set ->
            set.find { it.uri == uri }?.let { set.remove(it) }
        }
    }
}
