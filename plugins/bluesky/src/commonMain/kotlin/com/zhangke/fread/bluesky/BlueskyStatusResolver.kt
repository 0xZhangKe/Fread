package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.usecase.BskyStatusInteractiveUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.status.model.StatusInteraction
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyStatusResolver @Inject constructor(
    private val statusInteractive: BskyStatusInteractiveUseCase,
) : IStatusResolver {

    override suspend fun getStatus(
        role: IdentityRole,
        statusId: String,
        platform: BlogPlatform
    ): Result<Status>? {
        TODO("Not yet implemented")
    }

    override suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        minId: String?,
        maxId: String?
    ): Result<List<Status>>? {
        TODO("Not yet implemented")
    }

    override suspend fun interactive(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction
    ): Result<Status?>? {
        if (status.platform.protocol.notBluesky) return null
        return statusInteractive(role, status, interaction)
    }

    override suspend fun votePoll(
        role: IdentityRole,
        status: Status,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        TODO("Not yet implemented")
    }

    override suspend fun getStatusContext(
        role: IdentityRole,
        status: Status
    ): Result<StatusContext>? {
        TODO("Not yet implemented")
    }

    override suspend fun getSuggestionAccounts(role: IdentityRole): Result<List<BlogAuthor>>? {
        TODO("Not yet implemented")
    }

    override suspend fun getHashtag(
        role: IdentityRole,
        limit: Int,
        offset: Int
    ): Result<List<Hashtag>>? {
        TODO("Not yet implemented")
    }

    override suspend fun getPublicTimeline(
        role: IdentityRole,
        limit: Int,
        maxId: String?
    ): Result<List<Status>>? {
        TODO("Not yet implemented")
    }

    override suspend fun follow(
        role: IdentityRole,
        target: BlogAuthor
    ): Result<Unit>? {
        TODO("Not yet implemented")
    }

    override suspend fun unfollow(
        role: IdentityRole,
        target: BlogAuthor
    ): Result<Unit>? {
        TODO("Not yet implemented")
    }

    override suspend fun isFollowing(
        role: IdentityRole,
        target: BlogAuthor
    ): Result<Boolean>? {
        TODO("Not yet implemented")
    }

    override suspend fun translate(
        role: IdentityRole,
        status: Status,
        lan: String
    ): Result<BlogTranslation>? {
        TODO("Not yet implemented")
    }
}