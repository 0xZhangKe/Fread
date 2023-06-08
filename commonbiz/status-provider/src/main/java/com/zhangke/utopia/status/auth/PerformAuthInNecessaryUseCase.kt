package com.zhangke.utopia.status.auth

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class PerformAuthInNecessaryUseCase @Inject constructor(
    private val authValidateUseCase: SourceListAuthValidateUseCase,
    private val performAuthBySourceListUseCase: PerformAuthBySourceListUseCase,
) {

    suspend operator fun invoke(sourceUriList: List<String>): Result<Boolean> {
        val validateResult = authValidateUseCase(sourceUriList)
        if (validateResult.isFailure) return validateResult
        if (validateResult.getOrNull() == true) return Result.success(true)
        return performAuthBySourceListUseCase(sourceUriList)
    }
}
