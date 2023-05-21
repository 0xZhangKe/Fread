package com.zhangke.utopia.status.domain

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner
import com.zhangke.utopia.status.utils.collect
import com.zhangke.utopia.status.utils.findImplementers
import javax.inject.Inject

class FetchSourceFromOwnerUseCase @Inject constructor() {

    suspend operator fun invoke(owner: StatusSourceOwner): Result<List<StatusSource>> {
        return findImplementers<IFetchSourceFromOwnerUseCase>().map { it.invoke(owner) }.collect()
    }
}

interface IFetchSourceFromOwnerUseCase {

    suspend operator fun invoke(owner: StatusSourceOwner): Result<List<StatusSource>>
}
