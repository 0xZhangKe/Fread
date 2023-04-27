package com.zhangke.utopia.status.domain

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject

class FetchOwnerFromSourceUseCase @Inject constructor(
    private val useCaseList: List<IFetchOwnerFromSourceUseCase>,
) {

    suspend operator fun invoke(source: StatusSource): Result<StatusSourceOwner> {
        useCaseList.forEach { useCase ->
            useCase.invoke(source).getOrNull()?.let {
                return Result.success(it)
            }
        }
        return Result.failure(IllegalArgumentException("Can`t find ${source.uri} owner"))
    }
}

interface IFetchOwnerFromSourceUseCase {

    suspend operator fun invoke(source: StatusSource): Result<StatusSourceOwner?>
}
