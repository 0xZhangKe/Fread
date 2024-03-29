package com.zhangke.utopia.status.account

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AccountManager(
    private val accountManagerList: List<IAccountManager>,
) {

    suspend fun getAllLoggedAccount(): List<LoggedAccount> {
        return accountManagerList.flatMap { it.getAllLoggedAccount() }
    }

    fun getAllAccountFlow(): Flow<List<LoggedAccount>> {
        val flowList = accountManagerList.mapNotNull {
            it.getAllAccountFlow()
        }
        return combine(*flowList.toTypedArray()) {
            it.flatMap { list -> list }
        }
    }

    suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean> {
        return accountManagerList.mapFirst {
            it.checkPlatformLogged(platform)
        }
    }

    suspend fun launchAuthBySource(baseUrl: FormalBaseUrl): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(RuntimeException("Can't auth!"))
        for (manager in accountManagerList) {
            val launchResult = manager.launchAuth(baseUrl)
            if (launchResult != null) {
                result = launchResult
                if (result.getOrNull() == true) break
            }
        }
        return result
    }

    suspend fun logout(uri: FormalUri) {
        accountManagerList.forEach {
            if (it.logout(uri)) return@forEach
        }
    }
}

interface IAccountManager {

    suspend fun getAllLoggedAccount(): List<LoggedAccount>

    fun getAllAccountFlow(): Flow<List<LoggedAccount>>?

    suspend fun checkPlatformLogged(platform: BlogPlatform): Result<Boolean>?

    suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean>?

    suspend fun logout(uri: FormalUri): Boolean
}
