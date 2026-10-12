package com.zhangke.fread.signal.archive

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchResult
import com.zhangke.fread.status.source.StatusSource

class SignalArchiveSearchEngine : ISearchEngine {

    override suspend fun search(
        locator: PlatformLocator,
        query: String
    ): Result<List<SearchResult>> {
        return Result.success(emptyList())
    }

    override suspend fun searchStatus(
        locator: PlatformLocator,
        query: String,
        maxId: String?,
        sort: com.zhangke.fread.status.search.SearchStatusSort,
    ): Result<List<StatusUiState>> {
        return Result.success(emptyList())
    }

    override suspend fun searchHashtag(
        locator: PlatformLocator,
        query: String, offset: Int?,
    ): Result<List<Hashtag>> {
        return Result.success(emptyList())
    }

    override suspend fun searchAuthor(
        locator: PlatformLocator,
        query: String,
        offset: Int?,
    ): Result<List<BlogAuthor>> {
        return Result.success(emptyList())
    }

    override suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>> {
        return Result.success(emptyList())
    }
}
