package com.zhangke.fread.status.search

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.utils.collect

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

    suspend fun searchPlatform(
        query: String,
        offset: Int?
    ): Result<List<BlogPlatform>> {
        return engineList.map { it.searchPlatform(query, offset) }.collect()
    }

    suspend fun searchPlatformSnapshot(query: String): List<PlatformSnapshot>{
        return engineList.map { it.searchPlatformSnapshot(query) }.flatten()
    }

    suspend fun searchSource(role: IdentityRole, query: String): Result<List<StatusSource>> {
        return engineList.map { it.searchSource(role, query.trim()) }.collect()
    }

    suspend fun searchContent(
        role: IdentityRole,
        query: String,
    ): Result<List<SearchContentResult>> {
        return engineList.map { it.searchContent(role, query) }.flatten()
            .let { Result.success(it) }
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

    suspend fun searchPlatform(
        query: String,
        offset: Int?,
    ): Result<List<BlogPlatform>>

    suspend fun searchPlatformSnapshot(query: String): List<PlatformSnapshot>

    suspend fun searchSource(role: IdentityRole, query: String): Result<List<StatusSource>>

    suspend fun searchContent(role: IdentityRole, query: String): List<SearchContentResult>
}
