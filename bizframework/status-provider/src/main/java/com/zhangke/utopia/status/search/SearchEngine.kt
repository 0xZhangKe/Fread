package com.zhangke.utopia.status.search

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.utils.collect

class SearchEngine(
    private val engineList: List<ISearchEngine>,
) {

    suspend fun search(baseUrl: FormalBaseUrl, query: String): Result<List<SearchResult>> {
        return engineList.map { it.search(query.trim()) }.collect()
    }

    suspend fun searchStatus(baseUrl: FormalBaseUrl, query: String, maxId: String?): Result<List<Status>> {
        return engineList.map { it.searchStatus(query, maxId) }.collect()
    }

    suspend fun searchHashtag(baseUrl: FormalBaseUrl, query: String, offset: Int?): Result<List<Hashtag>> {
        return engineList.map { it.searchHashtag(query, offset) }.collect()
    }

    suspend fun searchAuthor(baseUrl: FormalBaseUrl, query: String, offset: Int?): Result<List<BlogAuthor>> {
        return engineList.map { it.searchAuthor(query, offset) }.collect()
    }

    suspend fun searchPlatform(baseUrl: FormalBaseUrl, query: String, offset: Int?): Result<List<BlogPlatform>> {
        return engineList.map { it.searchPlatform(query, offset) }.collect()
    }

    suspend fun searchSource(baseUrl: FormalBaseUrl, query: String): Result<List<StatusSource>> {
        return engineList.map { it.searchSource(query.trim()) }.collect()
    }

    suspend fun searchContent(baseUrl: FormalBaseUrl, query: String): Result<List<SearchContentResult>> {
        return engineList.mapNotNull { it.searchContent(query) }
            .collect()
    }
}

interface ISearchEngine {

    suspend fun search(query: String): Result<List<SearchResult>>

    suspend fun searchStatus(query: String, maxId: String?): Result<List<Status>>

    suspend fun searchHashtag(query: String, offset: Int?): Result<List<Hashtag>>

    suspend fun searchAuthor(query: String, offset: Int?): Result<List<BlogAuthor>>

    suspend fun searchPlatform(query: String, offset: Int?): Result<List<BlogPlatform>>

    suspend fun searchSource(query: String): Result<List<StatusSource>>

    suspend fun searchContent(query: String): Result<List<SearchContentResult>>?
}
