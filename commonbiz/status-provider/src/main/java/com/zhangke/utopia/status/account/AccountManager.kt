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

    suspend fun launchAuthBySource(source: StatusSource): Result<Boolean> {
        val launcher = accountManagerList.firstOrNull { it.applicable(source) }
            ?: return Result.failure(IllegalArgumentException("Illegal source: $source"))
        return launcher.launchAuthBySource(source)
    }
}

interface IAccountManager {

    suspend fun getAllLoggedAccount(): Result<List<LoggedAccount>>

    suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>
    ): Result<SourcesAuthValidateResult>

    fun applicable(source: StatusSource): Boolean

    suspend fun launchAuthBySource(source: StatusSource): Result<Boolean>
}
