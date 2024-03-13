package com.zhangke.utopia.rss

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.utopia.rss.internal.repo.RssStatusRepo
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.rss.internal.uri.isRssUri
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssStatusResolver @Inject constructor(
    private val uriTransformer: RssUriTransformer,
    private val rssStatusRepo: RssStatusRepo,
) : IStatusResolver {

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>>? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri)
            ?: return Result.failure(IllegalArgumentException("Unknown uri: $uri"))
        if (!maxId.isNullOrEmpty()) {
            return Result.success(emptyList())
        }
        val fetchResult = rssStatusRepo.getStatus(uriInsight)
        if (fetchResult.isFailure) {
            return Result.failure(fetchResult.exceptionOrThrow())
        }
        var finalReturnItems = fetchResult.getOrThrow()
        if (!sinceId.isNullOrEmpty()) {
            val sinceIndex = finalReturnItems.indexOfFirst { it.id == sinceId }
            if (sinceIndex >= 0) {
                finalReturnItems = finalReturnItems.subList(0, sinceIndex)
            }
        }
        return Result.success(finalReturnItems.takeLast(limit))
    }

    override suspend fun interactive(
        status: Status,
        interaction: StatusInteraction
    ): Result<Status>? {
        return null
    }

    override suspend fun votePoll(
        status: Status,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        return null
    }

    override suspend fun getStatusContext(status: Status): Result<StatusContext>? {
        if (!status.platform.protocol.isRssProtocol) return null
        return Result.success(
            StatusContext(
                ancestors = emptyList(),
                descendants = emptyList(),
            )
        )
    }
}
