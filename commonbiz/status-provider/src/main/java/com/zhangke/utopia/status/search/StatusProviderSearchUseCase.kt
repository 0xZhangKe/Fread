package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class StatusProviderSearchUseCase @Inject constructor(
    private val useCaseList: List<IStatusProviderSearchUseCase>
) {

    suspend operator fun invoke(query: String): Result<List<StatusProviderSearchResult>> {
        return useCaseList.map { it.invoke(query) }.collect()
    }
}

interface IStatusProviderSearchUseCase {

    suspend operator fun invoke(query: String): Result<List<StatusProviderSearchResult>>
}
