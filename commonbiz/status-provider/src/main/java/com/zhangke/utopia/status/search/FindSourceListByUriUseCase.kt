package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class FindSourceListByUriUseCase @Inject constructor(
    private val useCaseList: List<IFindSourceListByUriUseCase>,
) {

    suspend operator fun invoke(uri: String): Result<List<StatusSource>> {
        return useCaseList.map { it.invoke(uri) }.collect()
    }
}

interface IFindSourceListByUriUseCase {

    suspend operator fun invoke(uri: String): Result<List<StatusSource>>
}
