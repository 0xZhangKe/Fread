package com.zhangke.fread.bluesky

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

class BlueskyAccountManager @Inject constructor(): IAccountManager {

    override suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        TODO("Not yet implemented")
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>>? {
        TODO("Not yet implemented")
    }

    override suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>? {
        TODO("Not yet implemented")
    }

    override fun triggerLaunchAuth(baseUrl: FormalBaseUrl) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshAllAccountInfo(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        TODO("Not yet implemented")
    }

    override fun subscribeNotification() {
        TODO("Not yet implemented")
    }
}