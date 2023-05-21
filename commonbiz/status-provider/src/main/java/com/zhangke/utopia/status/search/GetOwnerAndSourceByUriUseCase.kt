package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusOwnerAndSources
import javax.inject.Inject

class GetOwnerAndSourceByUriUseCase @Inject constructor(
    private val findOwnerByUriUseCase: FindOwnerByUriUseCase,
    private val findSourceListByUriUseCase: FindSourceListByUriUseCase,
) {

    suspend operator fun invoke(
        uri: String
    ): Result<StatusOwnerAndSources> {
        val owner = findOwnerByUriUseCase(uri).getOrNull() ?: return Result.failure(
            IllegalArgumentException()
        )
        val sourceList =
            findSourceListByUriUseCase(uri).getOrNull() ?: return Result.failure(
                IllegalArgumentException()
            )
        return Result.success(StatusOwnerAndSources(owner, sourceList))
    }
}
