package com.zhangke.fread.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.push.PushManager
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
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
    private val pushManager: PushManager,
    private val applicationCoroutineScope: ApplicationCoroutineScope,
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

    fun observeAccount(baseUrl: FormalBaseUrl): Flow<ActivityPubLoggedAccount?> {
        return accountRepo.observeAccount(baseUrl)
    }

    override suspend fun refreshAllAccountInfo(): List<AccountRefreshResult> {
        return accountRepo.queryAll().map { loggedAccount ->
            val role =
                IdentityRole(accountUri = loggedAccount.uri, baseUrl = loggedAccount.baseUrl)
            val client = clientManager.getClient(role)
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

    override suspend fun logout(uri: FormalUri): Boolean {
        userUriTransformer.parse(uri) ?: return false
        val account = accountRepo.queryByUri(uri.toString())
        if (account != null) {
            val role = IdentityRole(accountUri = account.uri)
            pushManager.unsubscribe(role, account.userId)
        }
        accountRepo.deleteByUri(uri)
        loggedAccountProvider.removeAccount(uri)
        return true
    }

    suspend fun getAccount(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount? {
        return accountRepo.queryByBaseUrl(baseUrl)
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

    private suspend fun subscribeNotificationForAccount(account: ActivityPubLoggedAccount) {
        val role = IdentityRole(accountUri = account.uri)
        pushManager.subscribe(role, account.userId)
    }
}
