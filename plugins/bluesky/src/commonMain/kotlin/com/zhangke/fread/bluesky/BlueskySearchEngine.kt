package com.zhangke.fread.bluesky

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.fread.status.search.SearchResult
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.status.model.Status
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

class BlueskySearchEngine @Inject constructor(): ISearchEngine {

    override suspend fun search(
        role: IdentityRole,
        query: String
    ): Result<List<SearchResult>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchStatus(
        role: IdentityRole,
        query: String,
        maxId: String?
    ): Result<List<Status>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchHashtag(
        role: IdentityRole,
        query: String,
        offset: Int?
    ): Result<List<Hashtag>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchAuthor(
        role: IdentityRole,
        query: String,
        offset: Int?
    ): Result<List<BlogAuthor>> {
        TODO("Not yet implemented")
    }

    override fun searchAuthablePlatform(query: String): Flow<List<PlatformSnapshot>>? {
        TODO("Not yet implemented")
    }

    override suspend fun searchSource(
        role: IdentityRole,
        query: String
    ): Result<List<StatusSource>> {
        TODO("Not yet implemented")
    }

    override fun searchContent(
        role: IdentityRole,
        query: String
    ): Flow<List<SearchContentResult>> {
        TODO("Not yet implemented")
    }
}
