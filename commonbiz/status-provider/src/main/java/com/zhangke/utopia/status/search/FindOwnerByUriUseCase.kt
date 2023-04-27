package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject

class FindOwnerByUriUseCase @Inject constructor(
    private val useCaseList: List<IFindOwnerByUriUseCase>
) {

    suspend operator fun invoke(uri: String): Result<StatusSourceOwner?> {
        for (useCase in useCaseList) {
            val owner = useCase.invoke(uri).getOrNull()
            if (owner != null) return Result.success(owner)
        }
        return Result.success(null)
    }
}

interface IFindOwnerByUriUseCase {

    suspend operator fun invoke(uri: String): Result<StatusSourceOwner?>
}
