package com.zhangke.fread.status.search

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.utils.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class SearchEngine(
    private val engineList: List<ISearchEngine>,
) {

    suspend fun search(role: IdentityRole, query: String): Result<List<SearchResult>> {
        return engineList.map { it.search(role, query.trim()) }.collect()
    }

    suspend fun searchStatus(
        role: IdentityRole,
        query: String,
        maxId: String?
    ): Result<List<Status>> {
        return engineList.map { it.searchStatus(role, query, maxId) }.collect()
    }

    suspend fun searchHashtag(
        role: IdentityRole,
        query: String,
        offset: Int?
    ): Result<List<Hashtag>> {
        return engineList.map { it.searchHashtag(role, query, offset) }.collect()
    }

    suspend fun searchAuthor(
        role: IdentityRole,
        query: String,
        offset: Int?
    ): Result<List<BlogAuthor>> {
        return engineList.map { it.searchAuthor(role, query, offset) }.collect()
    }

    fun searchAuthablePlatform(
        query: String,
    ): Flow<Pair<String, List<PlatformSnapshot>>> {
        return engineList.mapNotNull { it.searchAuthablePlatform(query) }.merge().map {
            query to it
        }
    }

    suspend fun searchSource(role: IdentityRole, query: String): Result<List<StatusSource>> {
        return engineList.map { it.searchSource(role, query.trim()) }.collect()
    }

    fun searchContent(
        role: IdentityRole,
        query: String,
    ): Flow<Pair<String, List<SearchContentResult>>> {
        return engineList.map { it.searchContent(role, query) }
            .merge()
            .map { query to it }
    }
}

interface ISearchEngine {

    suspend fun search(role: IdentityRole, query: String): Result<List<SearchResult>>

    suspend fun searchStatus(
        role: IdentityRole,
        query: String,
        maxId: String?,
    ): Result<List<Status>>

    suspend fun searchHashtag(
        role: IdentityRole,
        query: String,
        offset: Int?,
    ): Result<List<Hashtag>>

    suspend fun searchAuthor(
        role: IdentityRole,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>>

    fun searchAuthablePlatform(
        query: String,
    ): Flow<List<PlatformSnapshot>>?

    suspend fun searchSource(role: IdentityRole, query: String): Result<List<StatusSource>>

    fun searchContent(
        role: IdentityRole,
        query: String
    ): Flow<List<SearchContentResult>>
}
