package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class AccountManager @Inject constructor(
    private val accountManagerList: List<IAccountManager>,
) {

    suspend fun getAllLoggedAccount(): Result<List<LoggedAccount>> {
        return accountManagerList.map { it.getAllLoggedAccount() }.collect()
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

    suspend fun launchAuthBySource(baseUrl: String): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(RuntimeException("Can't auth!"))
        for (manager in accountManagerList) {
            result = manager.launchAuthBySource(baseUrl)
            if (result.getOrNull() == true) break
        }
        return result
    }
}

interface IAccountManager {

    suspend fun getAllLoggedAccount(): Result<List<LoggedAccount>>

    suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>
    ): Result<SourcesAuthValidateResult>

    suspend fun launchAuthBySource(baseUrl: String): Result<Boolean>
}
