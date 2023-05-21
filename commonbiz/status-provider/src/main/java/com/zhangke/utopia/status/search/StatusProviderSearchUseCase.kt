package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.utils.collect
import com.zhangke.utopia.status.utils.findImplementers
import javax.inject.Inject

class StatusProviderSearchUseCase @Inject constructor() {

    suspend operator fun invoke(query: String): Result<List<StatusProviderSearchResult>> {
        return findImplementers<IStatusProviderSearchUseCase>().map { it.invoke(query) }.collect()
    }
}

interface IStatusProviderSearchUseCase {

    suspend operator fun invoke(query: String): Result<List<StatusProviderSearchResult>>
}
