package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.utils.collect

class SearchEngine(
    private val engineList: List<IUtopiaSearchEngine>,
) {

    suspend fun search(query: String): Result<List<SearchResult>> {
        return engineList.map { it.search(query) }.collect()
    }
}

interface IUtopiaSearchEngine {

    suspend fun search(query: String): Result<List<SearchResult>>
}
