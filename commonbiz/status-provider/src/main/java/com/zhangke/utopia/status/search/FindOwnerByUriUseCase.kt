package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSourceOwner
import com.zhangke.utopia.status.utils.findImplementers
import javax.inject.Inject

class FindOwnerByUriUseCase @Inject constructor() {

    suspend operator fun invoke(uri: String): Result<StatusSourceOwner?> {
        val useCaseList = findImplementers<IFindOwnerByUriUseCase>()
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
