package com.zhangke.utopia.rss

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RssAccountManager @Inject constructor() : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return emptyList()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>>? {
        return null
    }

    override suspend fun validateAuthOfSourceList(sourceList: List<StatusSource>): Result<SourcesAuthValidateResult>? {
        return null
    }

    override suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean>? {
        return null
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        return false
    }
}
