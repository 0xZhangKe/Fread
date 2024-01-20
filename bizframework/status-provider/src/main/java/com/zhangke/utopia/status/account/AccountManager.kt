package com.zhangke.utopia.status.account

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.source.StatusSource
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
        val flowList = accountManagerList.map {
            it.getAllAccountFlow()
        }
        return combine(*flowList.toTypedArray()) {
            it.flatMap { list -> list }
        }
    }

    suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        val resultList = accountManagerList.map {
            it.validateAuthOfSourceList(sourceList)
        }
        resultList.firstOrNull { it.isFailure }?.let { return it }
        val validateList = mutableListOf<StatusSource>()
        val invalidateList = mutableListOf<StatusSource>()
        resultList.map { it.getOrThrow() }
            .forEach {
                validateList += it.validateList
                invalidateList += it.invalidateList
            }
        return Result.success(
            SourcesAuthValidateResult(
                validateList = validateList,
                invalidateList = invalidateList,
            )
        )
    }

    suspend fun launchAuthBySource(baseUrl: FormalBaseUrl): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(RuntimeException("Can't auth!"))
        for (manager in accountManagerList) {
            result = manager.launchAuth(baseUrl)
            if (result.getOrNull() == true) break
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

    fun getAllAccountFlow(): Flow<List<LoggedAccount>>

    suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>
    ): Result<SourcesAuthValidateResult>

    suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean>

    suspend fun logout(uri: FormalUri): Boolean
}
