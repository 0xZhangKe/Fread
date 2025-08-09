package com.zhangke.fread.status.account

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.Relationships
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

    suspend fun triggerAuthBySource(platform: BlogPlatform) {
        for (manager in accountManagerList) {
            manager.triggerLaunchAuth(platform)
        }
    }

    suspend fun refreshAllAccountInfo(): List<AccountRefreshResult> {
        return accountManagerList.flatMap {
            it.refreshAllAccountInfo()
        }
    }

    suspend fun logout(account: LoggedAccount) {
        accountManagerList.forEach {
            if (it.logout(account)) return@forEach
        }
    }

    suspend fun selectContentWithAccount(
        contentList: List<FreadContent>,
        account: LoggedAccount,
    ): List<FreadContent> {
        return accountManagerList.flatMap { manager ->
            manager.selectContentWithAccount(contentList, account)
        }
    }

    fun subscribeNotification() {
        for (manager in accountManagerList) {
            manager.subscribeNotification()
        }
    }

    suspend fun getRelationships(
        account: LoggedAccount,
        accounts: List<BlogAuthor>,
    ): Result<Map<FormalUri, Relationships>> {
        val allResult = mutableMapOf<FormalUri, Relationships>()
        for (manager in accountManagerList) {
            manager.getRelationships(account, accounts).onSuccess { map -> allResult.putAll(map) }
        }
        return Result.success(allResult)
    }

    suspend fun unblockAccount(
        account: LoggedAccount,
        user: BlogAuthor,
    ): Result<Unit> {
        return accountManagerList.firstNotNullOf { it.unblockAccount(account, user) }
    }
}

interface IAccountManager {

    suspend fun getAllLoggedAccount(): List<LoggedAccount>

    fun getAllAccountFlow(): Flow<List<LoggedAccount>>?

    suspend fun triggerLaunchAuth(platform: BlogPlatform)

    suspend fun refreshAllAccountInfo(): List<AccountRefreshResult>

    suspend fun logout(account: LoggedAccount): Boolean

    suspend fun selectContentWithAccount(
        contentList: List<FreadContent>,
        account: LoggedAccount,
    ): List<FreadContent>

    suspend fun getRelationships(
        account: LoggedAccount,
        accounts: List<BlogAuthor>,
    ): Result<Map<FormalUri, Relationships>>

    suspend fun unblockAccount(
        account: LoggedAccount,
        user: BlogAuthor,
    ): Result<Unit>?

    fun subscribeNotification()
}

sealed interface AccountRefreshResult {

    val account: LoggedAccount

    data class Success(override val account: LoggedAccount) : AccountRefreshResult

    data class Failure(
        override val account: LoggedAccount,
        val error: Throwable,
    ) : AccountRefreshResult
}
