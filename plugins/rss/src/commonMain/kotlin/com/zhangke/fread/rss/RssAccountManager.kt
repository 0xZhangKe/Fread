package com.zhangke.fread.rss

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.notRss
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

class RssAccountManager @Inject constructor() : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return emptyList()
    }

    override suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>? {
        if (platform.protocol.notRss) {
            return null
        }
        return Result.success(true)
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>>? {
        return null
    }

    override fun triggerLaunchAuth(baseUrl: FormalBaseUrl) {
        // no-op
    }

    override suspend fun refreshAllAccountInfo(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        return false
    }
}
