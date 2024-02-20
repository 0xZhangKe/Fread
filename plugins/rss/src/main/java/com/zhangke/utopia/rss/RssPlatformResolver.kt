package com.zhangke.utopia.rss

import com.zhangke.utopia.rss.internal.platform.RssPlatformTransformer
import com.zhangke.utopia.rss.internal.repo.RssRepo
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.rss.internal.uri.isRssUri
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

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

    override suspend fun getAllRecordedPlatformForLogin(): List<BlogPlatform> {
        return emptyList()
    }
}
