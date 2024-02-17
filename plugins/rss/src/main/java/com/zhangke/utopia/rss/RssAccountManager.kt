package com.zhangke.utopia.rss

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class RssAccountManager @Inject constructor() : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return emptyList()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>> {
        return emptyFlow()
    }

    override suspend fun validateAuthOfSourceList(sourceList: List<StatusSource>): Result<SourcesAuthValidateResult> {
        return Result.failure(IllegalArgumentException("Not implemented"))
    }

    override suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        return false
    }
}
