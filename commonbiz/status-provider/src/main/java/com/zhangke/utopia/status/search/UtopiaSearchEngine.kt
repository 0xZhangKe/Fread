package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class UtopiaSearchEngine @Inject constructor(
    private val engineList: Set<@JvmSuppressWildcards IUtopiaSearchEngine>,
    ) {

    suspend fun search(query: String): Result<List<SearchResult>>{
        return engineList.map { it.search(query) }.collect()
    }
}

interface IUtopiaSearchEngine{

    suspend fun search(query: String): Result<List<SearchResult>>
}
