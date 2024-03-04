package com.zhangke.utopia.rss

import android.util.Log
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.utopia.rss.internal.adapter.BlogAuthorAdapter
import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.platform.RssPlatformTransformer
import com.zhangke.utopia.rss.internal.repo.RssRepo
import com.zhangke.utopia.rss.internal.source.RssSourceTransformer
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.search.ISearchEngine
import com.zhangke.utopia.status.search.SearchContentResult
import com.zhangke.utopia.status.search.SearchResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class RssSearchEngine @Inject constructor(
    private val rssPlatformTransformer: RssPlatformTransformer,
    private val rssSourceTransformer: RssSourceTransformer,
    private val rssRepo: RssRepo,
    private val bogAuthorAdapter: BlogAuthorAdapter,
    private val rssUriTransformer: RssUriTransformer,
) : ISearchEngine {

    override suspend fun search(query: String): Result<List<SearchResult>> {
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

    override suspend fun searchStatus(query: String, maxId: String?): Result<List<Status>> {
        return Result.success(emptyList())
    }

    override suspend fun searchHashtag(query: String, offset: Int?): Result<List<Hashtag>> {
        return Result.success(emptyList())
    }

    override suspend fun searchAuthor(query: String, offset: Int?): Result<List<BlogAuthor>> {
        if (offset != null && offset > 0) {
            return Result.success(emptyList())
        }
        return searchAuthorByUrl(query).map {
            it?.let { listOf(it) } ?: emptyList()
        }
    }

    override suspend fun searchPlatform(query: String, offset: Int?): Result<List<BlogPlatform>> {
        return queryWithChannelByUrl(
            query = query,
            defaultResult = emptyList(),
            block = { channel, uriInsight ->
                listOf(rssPlatformTransformer.create(uriInsight, channel))
            }
        )
    }

    override suspend fun searchSource(query: String): Result<List<StatusSource>> {
        return queryWithChannelByUrl(
            query = query,
            defaultResult = emptyList(),
            block = { source, uriInsight ->
                listOf(rssSourceTransformer.createSource(uriInsight, source))
            }
        )
    }

    override suspend fun searchContent(query: String): Result<List<SearchContentResult>>? {
        val result = queryWithChannelByUrl(
            query = query,
            defaultResult = null,
            block = { source, uriInsight ->
                rssSourceTransformer.createSource(uriInsight, source)
            }
        )
        if (result.isFailure) {
            return Result.failure(result.exceptionOrThrow())
        }
        val contentResult = result.getOrNull() ?: return null
        return Result.success(listOf(SearchContentResult.Source(contentResult)))
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
        Log.d("U_TEST", "query: $query, url: $url")
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
