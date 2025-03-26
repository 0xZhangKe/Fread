package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreen
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

class BlueskyAccountManager @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
) : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return accountManager.getAllAccount()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>> {
        return accountManager.getAllAccountFlow()
    }

    override suspend fun triggerLaunchAuth(platform: BlogPlatform) {
        if (platform.protocol.notBluesky) return
        GlobalScreenNavigation.navigate(
            AddBlueskyContentScreen(
                baseUrl = platform.baseUrl,
                loginMode = true
            )
        )
    }

    override suspend fun refreshAllAccountInfo(): List<AccountRefreshResult> {
        return accountManager.refreshAccountProfile()
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        accountManager.logout(uri)
        return true
    }

    override fun subscribeNotification() {

    }
}
