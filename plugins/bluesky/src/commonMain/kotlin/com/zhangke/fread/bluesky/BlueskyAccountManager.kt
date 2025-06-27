package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreen
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
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

    override suspend fun logout(account: LoggedAccount): Boolean {
        if (account !is BlueskyLoggedAccount) return false
        accountManager.logout(account.uri)
        return true
    }

    override fun subscribeNotification() {

    }

    override suspend fun selectContentWithAccount(
        contentList: List<FreadContent>,
        account: LoggedAccount
    ): List<FreadContent> {
        return contentList.filterIsInstance<BlueskyContent>()
            .filter { it.baseUrl == account.platform.baseUrl }
    }
}
