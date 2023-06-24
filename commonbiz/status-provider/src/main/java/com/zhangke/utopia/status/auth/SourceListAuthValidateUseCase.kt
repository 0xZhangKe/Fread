package com.zhangke.utopia.status.auth

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class SourceListAuthValidateUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards ISourceListAuthValidateUseCase>,
) {

    suspend operator fun invoke(sourceList: List<StatusSource>): Result<SourcesAuthValidateResult> {
        val resultList = useCases.map {
            it(sourceList)
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
}

interface ISourceListAuthValidateUseCase {

    suspend operator fun invoke(sourceList: List<StatusSource>): Result<SourcesAuthValidateResult>
}

class SourcesAuthValidateResult(
    val validateList: List<StatusSource>,
    val invalidateList: List<StatusSource>,
)
