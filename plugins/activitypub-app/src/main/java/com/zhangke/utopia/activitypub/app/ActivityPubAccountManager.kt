package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.utopia.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.model.notActivityPub
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivityPubAccountManager @Inject constructor(
    private val oAuthor: ActivityPubOAuthor,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val userUriTransformer: UserUriTransformer,
) : IAccountManager {

    init {
        ApplicationScope.launch {
            accountRepo.getAllAccountFlow()
                .collect {
                    for (account in it) {
                        loggedAccountProvider.addAccount(account)
                    }
                }
        }
    }

    override suspend fun getAllLoggedAccount(): List<ActivityPubLoggedAccount> {
        return accountRepo.queryAll()
    }

    override fun getAllAccountFlow(): Flow<List<ActivityPubLoggedAccount>> {
        return accountRepo.getAllAccountFlow()
    }

    override suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>? {
        if (platform.protocol.notActivityPub) return null
        val account = getAllLoggedAccount().firstOrNull {
            it.baseUrl == platform.baseUrl
        }
        return Result.success(account != null)
    }

    override suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean> {
        return oAuthor.startOauth(baseUrl)
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        userUriTransformer.parse(uri) ?: return false
        accountRepo.deleteByUri(uri)
        loggedAccountProvider.removeAccount(uri)
        return true
    }

    suspend fun getAccount(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount?{
        return accountRepo.queryByBaseUrl(baseUrl)
    }
}
