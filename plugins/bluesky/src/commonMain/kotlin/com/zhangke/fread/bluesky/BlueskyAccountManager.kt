package com.zhangke.fread.bluesky

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

class BlueskyAccountManager @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
): IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return accountManager.getAllAccount()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>>? {
        return accountManager.getAllAccountFlow()
    }

    override suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>? {
        if (platform.protocol.notBluesky) return null
        return Result.success(false)
    }

    override fun triggerLaunchAuth(baseUrl: FormalBaseUrl) {

    }

    override suspend fun refreshAllAccountInfo(): Result<Unit> {
        accountManager.refreshAccountProfile()
        return Result.success(Unit)
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        accountManager.logout(uri)
        return true
    }

    override fun subscribeNotification() {

    }
}
