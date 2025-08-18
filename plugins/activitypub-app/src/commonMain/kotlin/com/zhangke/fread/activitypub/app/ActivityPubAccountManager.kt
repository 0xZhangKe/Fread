package com.zhangke.fread.activitypub.app

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.push.ActivityPubPushManager
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.ActivityPubAccountLogoutUseCase
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.LoggedAccountDetail
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class ActivityPubAccountManager @Inject constructor(
    private val oAuthor: ActivityPubOAuthor,
    private val clientManager: ActivityPubClientManager,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val userUriTransformer: UserUriTransformer,
    private val accountAdapter: ActivityPubLoggedAccountAdapter,
    private val activityPubPushManager: ActivityPubPushManager,
    private val applicationCoroutineScope: ApplicationCoroutineScope,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val accountLogout: ActivityPubAccountLogoutUseCase,
) : IAccountManager {

    init {
        applicationCoroutineScope.launch {
            accountRepo.getAllAccountFlow()
                .collect {
                    clientManager.clearCache()
                    loggedAccountProvider.updateAccounts(it)
                }
        }
    }

    override suspend fun getAllLoggedAccount(): List<ActivityPubLoggedAccount> {
        return accountRepo.queryAll()
    }

    override fun getAllAccountFlow(): Flow<List<ActivityPubLoggedAccount>> {
        return accountRepo.getAllAccountFlow()
    }

    override fun getAllAccountDetailFlow(): Flow<List<LoggedAccountDetail>>? {
        return accountRepo.getAllAccountFlow()
            .map { list ->
                list.map { accountEntityAdapter.convertLoggedAccountDetail(it) }
            }
    }

    fun observeAccount(accountUri: FormalUri): Flow<ActivityPubLoggedAccount?> {
        return accountRepo.observeAccount(accountUri.toString())
    }

    override suspend fun refreshAllAccountInfo(): List<AccountRefreshResult> {
        return accountRepo.queryAll().map { loggedAccount ->
            val locator =
                PlatformLocator(accountUri = loggedAccount.uri, baseUrl = loggedAccount.baseUrl)
            val client = clientManager.getClient(locator)
            val result = client.accountRepo
                .getAccount(loggedAccount.userId)
                .mapCatching {
                    val newAccount = accountAdapter.createFromAccount(
                        platform = loggedAccount.platform,
                        account = it,
                        token = loggedAccount.token,
                    )
                    accountRepo.update(newAccount)
                    newAccount
                }
            if (result.isFailure) {
                AccountRefreshResult.Failure(loggedAccount, result.exceptionOrThrow())
            } else {
                AccountRefreshResult.Success(result.getOrThrow())
            }
        }
    }

    override suspend fun triggerLaunchAuth(platform: BlogPlatform) {
        if (platform.protocol.notActivityPub) return
        oAuthor.startOauth(platform.baseUrl)
    }

    override suspend fun logout(account: LoggedAccount): Boolean {
        if (account !is ActivityPubLoggedAccount) {
            return false
        }
        accountLogout(account)
        return true
    }

    override fun subscribeNotification() {
        applicationCoroutineScope.launch {
            accountRepo.queryAll().forEach { account ->
                subscribeNotificationForAccount(account)
            }
            accountRepo.onNewAccountFlow.collect {
                subscribeNotificationForAccount(it)
            }
        }
    }

    override suspend fun cancelFollowRequest(
        account: LoggedAccount,
        user: BlogAuthor,
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        if (user.userId.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("User ID cannot be null or empty"))
        }
        val locator = PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(locator)
            .accountRepo
            .unfollow(user.userId!!)
            .map { }
    }

    private suspend fun subscribeNotificationForAccount(account: ActivityPubLoggedAccount) {
        val role = PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        activityPubPushManager.subscribe(role, account.userId)
    }

    override suspend fun getRelationships(
        account: LoggedAccount,
        accounts: List<BlogAuthor>,
    ): Result<Map<FormalUri, Relationships>> {
        val userIdList = accounts.filter { userUriTransformer.parse(it.uri) != null }
            .mapNotNull { it.userId }
        if (userIdList.isEmpty()) return Result.success(emptyMap())
        val locator = PlatformLocator(
            baseUrl = account.platform.baseUrl,
            accountUri = account.uri,
        )
        return clientManager.getClient(locator)
            .accountRepo
            .getRelationships(idList = userIdList)
            .map { list ->
                val relationships = mutableMapOf<FormalUri, Relationships>()
                for (entity in list) {
                    val user = accounts.firstOrNull { it.userId == entity.id }
                    if (user != null) {
                        relationships[user.uri] = accountEntityAdapter.convertRelationship(entity)
                    }
                }
                relationships
            }
    }

    override suspend fun unblockAccount(
        account: LoggedAccount,
        user: BlogAuthor
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        if (user.userId.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("User ID cannot be null or empty"))
        }
        val locator = PlatformLocator(
            baseUrl = account.platform.baseUrl,
            accountUri = account.uri,
        )
        return clientManager.getClient(locator)
            .accountRepo
            .unblock(user.userId!!)
            .map { }
    }

    override suspend fun selectContentWithAccount(
        contentList: List<FreadContent>,
        account: LoggedAccount,
    ): List<FreadContent> {
        return contentList.filterIsInstance<ActivityPubContent>()
            .filter { it.baseUrl == account.platform.baseUrl }
    }
}
