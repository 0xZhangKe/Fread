package com.zhangke.fread.rss

import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

class RssAccountManager @Inject constructor() : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return emptyList()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>>? {
        return null
    }

    override suspend fun triggerLaunchAuth(platform: BlogPlatform) {
        // no-op
    }

    override suspend fun refreshAllAccountInfo(): List<AccountRefreshResult> {
        return emptyList()
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        return false
    }

    override fun subscribeNotification() {}
}
