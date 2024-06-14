package com.zhangke.utopia.rss

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.model.notRss
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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

    override suspend fun logout(uri: FormalUri): Boolean {
        return false
    }
}
