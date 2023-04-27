package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusOwnerAndSources
import javax.inject.Inject

class GetOwnerAndSourceFromSearchResultUseCase @Inject constructor(
    private val findOwnerByUriUseCase: FindOwnerByUriUseCase,
    private val findSourceListByUriUseCase: FindSourceListByUriUseCase,
) {

    suspend operator fun invoke(
        result: StatusProviderSearchResult
    ): Result<StatusOwnerAndSources> {
        val owner = findOwnerByUriUseCase(result.uri).getOrNull() ?: return Result.failure(
            IllegalArgumentException()
        )
        val sourceList =
            findSourceListByUriUseCase(result.uri).getOrNull() ?: return Result.failure(
                IllegalArgumentException()
            )
        return Result.success(StatusOwnerAndSources(owner, sourceList))
    }
}
