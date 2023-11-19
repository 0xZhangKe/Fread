package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.platform.BlogPlatform
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

    suspend fun launchAuthBySource(platform: BlogPlatform): Result<Boolean> {
        val launcher = accountManagerList.firstOrNull { it.applicable(platform) }
            ?: return Result.failure(IllegalArgumentException("Illegal platform: $platform"))
        return launcher.launchAuthBySource(platform)
    }
}

interface IAccountManager {

    suspend fun getAllLoggedAccount(): Result<List<LoggedAccount>>

    suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>
    ): Result<SourcesAuthValidateResult>

    fun applicable(platform: BlogPlatform): Boolean

    suspend fun launchAuthBySource(platform: BlogPlatform): Result<Boolean>
}
