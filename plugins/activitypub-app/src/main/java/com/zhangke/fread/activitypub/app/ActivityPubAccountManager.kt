package com.zhangke.fread.activitypub.app

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.common.di.ApplicationScope
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
) : IAccountManager {

    init {
        ApplicationScope.launch {
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

    override suspend fun refreshAllAccountInfo(): Result<Unit> {
        val results = accountRepo.queryAll()
            .map { loggedAccount ->
                val role =
                    IdentityRole(accountUri = loggedAccount.uri, baseUrl = loggedAccount.baseUrl)
                val client = clientManager.getClient(role)
                client.accountRepo
                    .getAccount(loggedAccount.userId)
                    .mapCatching {
                        val entity = accountAdapter.createFromAccount(
                            platform = loggedAccount.platform,
                            account = it,
                            token = loggedAccount.token,
                        )
                        accountRepo.update(entity)
                    }
            }
        if (results.isEmpty()) return Result.success(Unit)
        val successResult = results.firstOrNull { it.isSuccess }
        if (successResult == null) {
            return results.first()
        }
        return successResult
    }

    override suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>? {
        if (platform.protocol.notActivityPub) return null
        val account = getAllLoggedAccount().firstOrNull {
            it.baseUrl == platform.baseUrl
        }
        return Result.success(account != null)
    }

    override fun triggerLaunchAuth(baseUrl: FormalBaseUrl) {
        oAuthor.startOauth(baseUrl)
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        userUriTransformer.parse(uri) ?: return false
        accountRepo.deleteByUri(uri)
        loggedAccountProvider.removeAccount(uri)
        return true
    }

    suspend fun getAccount(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount? {
        return accountRepo.queryByBaseUrl(baseUrl)
    }
}
