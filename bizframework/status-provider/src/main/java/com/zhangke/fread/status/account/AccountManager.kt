package com.zhangke.fread.status.account

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
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

    fun triggerAuthBySource(baseUrl: FormalBaseUrl) {
        for (manager in accountManagerList) {
            manager.triggerLaunchAuth(baseUrl)
        }
    }

    suspend fun refreshAllAccountInfo(): Result<Unit> {
        val resultList = accountManagerList.map {
            it.refreshAllAccountInfo()
        }
        val successResult = resultList.firstOrNull { it.isSuccess }
        if (successResult == null) {
            return resultList.first()
        }
        return successResult
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

    fun triggerLaunchAuth(baseUrl: FormalBaseUrl)

    suspend fun refreshAllAccountInfo(): Result<Unit>

    suspend fun logout(uri: FormalUri): Boolean
}
