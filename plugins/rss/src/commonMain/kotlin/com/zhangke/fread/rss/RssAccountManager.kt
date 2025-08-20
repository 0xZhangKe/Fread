package com.zhangke.fread.rss

import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.Relationships
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

    override suspend fun triggerLaunchAuth(platform: BlogPlatform, account: LoggedAccount?) {
        // no-op
    }

    override suspend fun refreshAllAccountInfo(): List<AccountRefreshResult> {
        return emptyList()
    }

    override suspend fun logout(account: LoggedAccount): Boolean {
        return false
    }

    override fun subscribeNotification() {}

    override suspend fun getRelationships(
        account: LoggedAccount,
        accounts: List<BlogAuthor>,
    ): Result<Map<FormalUri, Relationships>> {
        return Result.success(emptyMap())
    }

    override suspend fun cancelFollowRequest(
        account: LoggedAccount,
        user: BlogAuthor,
    ): Result<Unit>? {
        return null
    }

    override suspend fun unblockAccount(
        account: LoggedAccount,
        user: BlogAuthor
    ): Result<Unit>? {
        return null
    }

    override suspend fun selectContentWithAccount(
        contentList: List<FreadContent>,
        account: LoggedAccount
    ): List<FreadContent> {
        return emptyList()
    }
}
