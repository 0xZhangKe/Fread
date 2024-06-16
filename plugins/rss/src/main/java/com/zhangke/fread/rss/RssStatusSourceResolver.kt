package com.zhangke.fread.rss

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.rss.internal.adapter.RssStatusAdapter
import com.zhangke.fread.rss.internal.repo.RssRepo
import com.zhangke.fread.rss.internal.rss.RssFetcher
import com.zhangke.fread.rss.internal.source.RssSourceTransformer
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RssStatusSourceResolver @Inject constructor(
    private val rssUriTransformer: RssUriTransformer,
    private val rssSourceTransformer: RssSourceTransformer,
    private val rssStatusAdapter: RssStatusAdapter,
    private val rssRepo: RssRepo,
) : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(
        role: IdentityRole?,
        uri: FormalUri
    ): Result<StatusSource?> {
        if (!uri.isRssUri) return Result.success(null)
        val uriInsight = rssUriTransformer.parse(uri) ?: return Result.failure(
            IllegalArgumentException("Unknown uri: $uri")
        )
        val sourceResult = rssRepo.getRssSource(uriInsight.url)
        if (sourceResult.isFailure) {
            return Result.failure(sourceResult.exceptionOrThrow())
        }
        val source = sourceResult.getOrThrow() ?: return Result.success(null)
        return rssSourceTransformer.createSource(uriInsight, source)
            .let { Result.success(it) }
    }

    override suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return rssRepo.sourceChangedFlow
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource> {
        return RssFetcher.fetchRss(rssUrl)
            .map { it.first }
            .map {
                val uri = rssUriTransformer.build(rssUrl)
                val uriInsight = RssUriInsight(uri, rssUrl)
                rssSourceTransformer.createSource(uriInsight, it)
            }
    }
}
