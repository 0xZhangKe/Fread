package com.zhangke.fread.rss

import com.zhangke.fread.rss.internal.platform.RssPlatformTransformer
import com.zhangke.fread.rss.internal.repo.RssRepo
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class RssPlatformResolver @Inject constructor(
    private val rssPlatformTransformer: RssPlatformTransformer,
    private val rssUriTransformer: RssUriTransformer,
    private val rssRepo: RssRepo,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        if (!sourceUri.isRssUri) return Result.success(null)
        val uriInsight = rssUriTransformer.parse(sourceUri) ?: return Result.failure(
            IllegalArgumentException("Unknown uri: $sourceUri")
        )
        return rssRepo.getRssSource(uriInsight.url)
            .map { source ->
                source?.let { rssPlatformTransformer.create(uriInsight, it) }
            }
    }

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return emptyList()
    }

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        return null
    }
}
