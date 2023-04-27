package com.zhangke.utopia.status.domain

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner
import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class FetchSourceFromOwnerUseCase @Inject constructor(
    private val useCaseList: List<IFetchSourceFromOwnerUseCase>,
) {

    suspend operator fun invoke(owner: StatusSourceOwner): Result<List<StatusSource>> {
        return useCaseList.map { it.invoke(owner) }.collect()
    }
}

interface IFetchSourceFromOwnerUseCase {

    suspend operator fun invoke(owner: StatusSourceOwner): Result<List<StatusSource>>
}
