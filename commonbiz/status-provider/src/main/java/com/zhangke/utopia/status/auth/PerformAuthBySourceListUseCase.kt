package com.zhangke.utopia.status.auth

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class PerformAuthBySourceListUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards IPerformAuthBySourceListUseCase>,
) {

    suspend operator fun invoke(sourceUriList: List<String>): Result<Boolean> {
        return useCases.map { it(sourceUriList).isSuccess }
            .reduce { acc, b -> acc && b }
            .let { Result.success(it) }
    }
}

interface IPerformAuthBySourceListUseCase {

    suspend operator fun invoke(sourceUriList: List<String>): Result<Boolean>

}
