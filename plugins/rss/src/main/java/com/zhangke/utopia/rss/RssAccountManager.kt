package com.zhangke.utopia.rss

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.rss.internal.platform.RssPlatformTransformer
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RssAccountManager @Inject constructor(
    private val platformTransformer: RssPlatformTransformer,
) : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return emptyList()
    }

    override suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>? {
        if (platform.protocol.notRssProtocol) {
            return null
        }
        return Result.success(true)
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>>? {
        return null
    }

    override suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean>? {
        return null
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        return false
    }
}
