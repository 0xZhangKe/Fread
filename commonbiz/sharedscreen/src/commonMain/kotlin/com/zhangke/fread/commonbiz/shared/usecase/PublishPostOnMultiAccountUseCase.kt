package com.zhangke.fread.commonbiz.shared.usecase

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.publish.PublishingPost
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class PublishPostOnMultiAccountUseCase (
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(
        accounts: List<LoggedAccount>,
        publishingPost: PublishingPost,
    ): Result<Unit> {
        val publishManager = statusProvider.publishManager
        val results = supervisorScope {
            accounts.map {
                async { it to publishManager.publish(it, publishingPost) }
            }.awaitAll()
        }
        if (results.any { it.second.isFailure }) {
            val e = results.first { it.second.isFailure }.second.exceptionOrThrow()
            val successAccount = results.filter { it.second.isSuccess }.map { it.first }
            val failedAccounts = results.filter { it.second.isFailure }.map { it.first }
            return Result.failure(
                PublishingPartFailed(
                    successAccount = successAccount.map { it.uri.toString() },
                    failedAccounts = failedAccounts.map { it.uri.toString() },
                    e = e,
                )
            )
        } else {
            return Result.success(Unit)
        }
    }
} class PublishingPartFailed(
    val successAccount: List<String>,
    val failedAccounts: List<String>,
    val e: Throwable,
) : RuntimeException()