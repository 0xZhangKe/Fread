package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.collect

class SearchEngine(
    private val engineList: List<ISearchEngine>,
) {

    suspend fun query(query: String): Result<List<SearchResult>> {
        return engineList.map { it.query(query.trim()) }.collect()
    }

    suspend fun searchSource(query: String): Result<List<StatusSource>> {
        return engineList.map { it.searchSource(query.trim()) }.collect()
    }
}

interface ISearchEngine {

    suspend fun query(query: String): Result<List<SearchResult>>

    suspend fun searchSource(query: String): Result<List<StatusSource>>
}
