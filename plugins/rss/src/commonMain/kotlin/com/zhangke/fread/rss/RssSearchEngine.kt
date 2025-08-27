package com.zhangke.fread.rss

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.rss.internal.adapter.BlogAuthorAdapter
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.platform.RssPlatformTransformer
import com.zhangke.fread.rss.internal.repo.RssRepo
import com.zhangke.fread.rss.internal.source.RssSourceTransformer
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.search.SearchResult
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

class RssSearchEngine @Inject constructor(
    private val rssPlatformTransformer: RssPlatformTransformer,
    private val rssSourceTransformer: RssSourceTransformer,
    private val rssRepo: RssRepo,
    private val bogAuthorAdapter: BlogAuthorAdapter,
    private val rssUriTransformer: RssUriTransformer,
) : ISearchEngine {

    override suspend fun search(
        locator: PlatformLocator,
        query: String
    ): Result<List<SearchResult>> {
        val authorResult = searchAuthorByUrl(query)
        if (authorResult.isFailure) {
            return Result.failure(authorResult.exceptionOrThrow())
        }
        val searchResultList = mutableListOf<SearchResult>()
        authorResult.getOrNull()?.let {
            searchResultList += SearchResult.Author(it)
        }
        return Result.success(searchResultList)
    }

    override suspend fun searchStatus(
        locator: PlatformLocator,
        query: String,
        maxId: String?,
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
        if (offset != null && offset > 0) {
            return Result.success(emptyList())
        }
        return searchAuthorByUrl(query).map {
            it?.let { listOf(it) } ?: emptyList()
        }
    }

    override suspend fun searchSourceNoToken(query: String): Result<List<StatusSource>> {
        return queryWithChannelByUrl(
            query = query,
            defaultResult = emptyList(),
            block = { source, uriInsight ->
                listOf(rssSourceTransformer.createSource(uriInsight, source))
            }
        )
    }

    private suspend fun searchAuthorByUrl(query: String): Result<BlogAuthor?> {
        return queryWithChannelByUrl(
            query = query,
            defaultResult = null,
            block = { channel, uriInsight ->
                bogAuthorAdapter.createAuthor(uriInsight, channel)
            }
        )
    }

    private suspend fun <T> queryWithChannelByUrl(
        query: String,
        defaultResult: T,
        block: suspend (RssSource, RssUriInsight) -> T,
    ): Result<T> {
        val url = SimpleUri.parse(query)
            ?.toString()
            ?.let(::fixUrl) ?: return Result.success(defaultResult)
        val sourceResult = rssRepo.getRssSource(url)
        if (sourceResult.isFailure) {
            return Result.failure(sourceResult.exceptionOrThrow())
        }
        val source = sourceResult.getOrThrow() ?: return Result.success(defaultResult)
        val uri = rssUriTransformer.build(url)
        val uriInsight = RssUriInsight(uri, url)
        val result = block(source, uriInsight)
        return Result.success(result)
    }

    private fun fixUrl(url: String): String {
        return url.trim().removeSuffix("/")
    }
}
