package com.zhangke.utopia.status.domain

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner
import com.zhangke.utopia.status.utils.findImplementers
import javax.inject.Inject

class FetchOwnerFromSourceUseCase @Inject constructor() {

    suspend operator fun invoke(source: StatusSource): Result<StatusSourceOwner> {
        findImplementers<IFetchOwnerFromSourceUseCase>().forEach { useCase ->
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
