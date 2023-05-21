package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.collect
import com.zhangke.utopia.status.utils.findImplementers
import javax.inject.Inject

class FindSourceListByUriUseCase @Inject constructor() {

    suspend operator fun invoke(uri: String): Result<List<StatusSource>> {
        return findImplementers<IFindSourceListByUriUseCase>().map { it(uri) }.collect()
    }
}

interface IFindSourceListByUriUseCase {

    suspend operator fun invoke(uri: String): Result<List<StatusSource>>
}
