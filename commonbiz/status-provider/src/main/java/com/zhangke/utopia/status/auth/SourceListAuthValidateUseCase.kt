package com.zhangke.utopia.status.auth

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

/**
 * Validate SourceList Auth
 */
class SourceListAuthValidateUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards ISourceListAuthValidateUseCase>,
) {

    suspend operator fun invoke(sourceList: List<StatusSource>): Result<Boolean> {
        val resultList = useCases.map {
            it(sourceList)
        }
        resultList.firstOrNull { it.isFailure }?.let { return it }
        return resultList.reduce { acc, result ->
            Result.success(acc.getOrThrow() && result.getOrThrow())
        }
    }
}

interface ISourceListAuthValidateUseCase {

    suspend operator fun invoke(sourceList: List<StatusSource>): Result<Boolean>
}
