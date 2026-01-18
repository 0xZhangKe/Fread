package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreenNavKey
import com.zhangke.fread.bluesky.internal.usecase.UnblockUserWithoutUriUseCase
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.LoggedAccountDetail
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

class BlueskyAccountManager @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val unblockUserWithoutUri: UnblockUserWithoutUriUseCase,
) : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return accountManager.getAllAccount()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>> {
        return accountManager.getAllAccountFlow()
    }

    override fun getAllAccountDetailFlow(): Flow<List<LoggedAccountDetail>>? {
        return accountManager.getAllAccountFlow()
            .map { list ->
                list.map {
                    LoggedAccountDetail(
                        account = it,
                        author = it.user,
                    )
                }
            }
    }

    override suspend fun triggerLaunchAuth(platform: BlogPlatform, account: LoggedAccount?) {
        if (platform.protocol.notBluesky) return
        GlobalScreenNavigation.navigate(
            AddBlueskyContentScreenNavKey(
                baseUrl = platform.baseUrl,
                loginMode = true,
                avatar = account?.avatar,
                displayName = account?.userName,
                handle = account?.prettyHandle,
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

    override suspend fun getRelationships(
        account: LoggedAccount,
        accounts: List<BlogAuthor>,
    ): Result<Map<FormalUri, Relationships>> {
        // not implemented for Bluesky currently
        return Result.success(emptyMap())
    }

    override suspend fun cancelFollowRequest(
        account: LoggedAccount,
        user: BlogAuthor
    ): Result<Unit>? {
        return null
    }

    override suspend fun unblockAccount(
        account: LoggedAccount,
        user: BlogAuthor
    ): Result<Unit>? {
        if (account.platform.protocol.notBluesky) return null
        val locator = PlatformLocator(
            baseUrl = account.platform.baseUrl,
            accountUri = account.uri,
        )
        return unblockUserWithoutUri(locator, user)
    }

    override suspend fun selectContentWithAccount(
        contentList: List<FreadContent>,
        account: LoggedAccount
    ): List<FreadContent> {
        return contentList.filterIsInstance<BlueskyContent>()
            .filter { it.baseUrl == account.platform.baseUrl }
    }
}
