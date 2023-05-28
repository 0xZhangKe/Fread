package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class SearchStatusSourceUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards ISearchStatusSourceUseCase>,
) {

    suspend operator fun invoke(query: String): Result<List<StatusSource>> {
        return useCases.map {
            it.invoke(query)
        }.collect()
    }
}

interface ISearchStatusSourceUseCase {

    suspend operator fun invoke(query: String): Result<List<StatusSource>>
}
