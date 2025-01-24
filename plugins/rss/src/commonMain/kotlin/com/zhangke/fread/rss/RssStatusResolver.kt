package com.zhangke.fread.rss

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.rss.internal.repo.RssStatusRepo
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.status.model.StatusInteraction
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class RssStatusResolver @Inject constructor(
    private val uriTransformer: RssUriTransformer,
    private val rssStatusRepo: RssStatusRepo,
) : IStatusResolver {

    override suspend fun getStatus(
        role: IdentityRole,
        statusId: String,
        platform: BlogPlatform
    ): Result<Status>? {
        return null
    }

    override suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        minId: String?,
        maxId: String?,
    ): Result<List<Status>>? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri)
            ?: return Result.failure(IllegalArgumentException("Unknown uri: $uri"))
        val fetchResult = rssStatusRepo.getStatus(uriInsight)
            .map { it.sortedByDescending { status -> status.datetime } }
        if (fetchResult.isFailure) {
            return Result.failure(fetchResult.exceptionOrThrow())
        }
        var finalReturnItems = fetchResult.getOrThrow()
        if (finalReturnItems.isEmpty()) return Result.success(emptyList())
        if (!minId.isNullOrEmpty()) {
            val sinceIndex = finalReturnItems.indexOfFirst { it.id == minId }
            if (sinceIndex >= 0) {
                finalReturnItems = finalReturnItems.subList(0, sinceIndex)
            }
        }
        if (!maxId.isNullOrEmpty()){
            val maxIndex = finalReturnItems.indexOfFirst { it.id == maxId }
            if (maxIndex >= 0){
                finalReturnItems = finalReturnItems.subList(maxIndex, finalReturnItems.size)
            }
        }
        return Result.success(finalReturnItems.take(limit))
    }

    override suspend fun interactive(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction
    ): Result<Status?>? {
        return null
    }

    override suspend fun votePoll(
        role: IdentityRole,
        status: Status,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        return null
    }

    override suspend fun getStatusContext(
        role: IdentityRole,
        status: Status,
    ): Result<StatusContext>? {
        if (!status.platform.protocol.isRss) return null
        return Result.success(
            StatusContext(
                ancestors = emptyList(),
                status = null,
                descendants = emptyList(),
            )
        )
    }

    override suspend fun getSuggestionAccounts(role: IdentityRole): Result<List<BlogAuthor>>? {
        return null
    }

    override suspend fun getHashtag(
        role: IdentityRole,
        limit: Int,
        offset: Int
    ): Result<List<Hashtag>>? {
        return null
    }

    override suspend fun getPublicTimeline(
        role: IdentityRole,
        limit: Int,
        maxId: String?
    ): Result<List<Status>>? {
        return null
    }

    override suspend fun follow(role: IdentityRole, target: BlogAuthor): Result<Unit>? {
        return null
    }

    override suspend fun unfollow(role: IdentityRole, target: BlogAuthor): Result<Unit>? {
        return null
    }

    override suspend fun isFollowing(role: IdentityRole, target: BlogAuthor): Result<Boolean>? {
        return null
    }

    override suspend fun translate(
        role: IdentityRole,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>? {
        return null
    }
}
