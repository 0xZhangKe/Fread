package com.zhangke.fread.status.search

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.utils.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class SearchEngine(
    private val engineList: List<ISearchEngine>,
) {

    suspend fun search(locator: PlatformLocator, query: String): Result<List<SearchResult>> {
        return engineList.map { it.search(locator, query.trim()) }.collect()
    }

    suspend fun searchStatus(
        locator: PlatformLocator,
        query: String,
        maxId: String?
    ): Result<List<StatusUiState>> {
        return engineList.map { it.searchStatus(locator, query, maxId) }.collect()
    }

    suspend fun searchHashtag(
        locator: PlatformLocator,
        query: String,
        offset: Int?
    ): Result<List<Hashtag>> {
        return engineList.map { it.searchHashtag(locator, query, offset) }.collect()
    }

    suspend fun searchAuthor(
        locator: PlatformLocator,
        query: String,
        offset: Int?
    ): Result<List<BlogAuthor>> {
        return engineList.map { it.searchAuthor(locator, query, offset) }.collect()
    }

    suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>> {
        return engineList.map { it.searchSourceNoToken(query.trim()) }.collect()
    }
}

interface ISearchEngine {

    suspend fun search(locator: PlatformLocator, query: String): Result<List<SearchResult>>

    suspend fun searchStatus(
        locator: PlatformLocator,
        query: String,
        maxId: String?,
    ): Result<List<StatusUiState>>

    suspend fun searchHashtag(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>>

    suspend fun searchAuthor(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>>

    suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>>
}
