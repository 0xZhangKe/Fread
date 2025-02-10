package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.bluesky.internal.usecase.BskyStatusInteractiveUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetStatusContextUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class BlueskyStatusResolver @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val statusInteractive: BskyStatusInteractiveUseCase,
    private val getStatusContextFunction: GetStatusContextUseCase,
    private val userUriTransformer: UserUriTransformer,
) : IStatusResolver {

    override suspend fun getStatus(
        role: IdentityRole,
        statusId: String,
        platform: BlogPlatform
    ): Result<StatusUiState>? {
        TODO("Not yet implemented")
    }

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        maxId: String?,
    ): Result<PagedData<StatusUiState>>? {
        TODO("Not yet implemented")
    }

    override suspend fun interactive(
        role: IdentityRole,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>? {
        if (status.platform.protocol.notBluesky) return null
        return statusInteractive(role, status, type)
    }

    override suspend fun votePoll(
        role: IdentityRole,
        blog: Blog,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        TODO("Not yet implemented")
    }

    override suspend fun getStatusContext(
        role: IdentityRole,
        status: Status
    ): Result<StatusContext>? {
        if (status.platform.protocol.notBluesky) return null
        return getStatusContextFunction(role, status)
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
    ): Result<List<StatusUiState>>? {
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
        val did = userUriTransformer.parse(target.uri)?.did ?: return null
        val client = clientManager.getClient(role)
        val profileResult = client.getProfileCatching(GetProfileQueryParams(Did(did)))
        if (profileResult.isFailure) return Result.failure(profileResult.exceptionOrNull()!!)
        val profile = profileResult.getOrThrow()
        return Result.success(profile.viewer?.following?.atUri.isNullOrEmpty().not())
    }

    override suspend fun translate(
        role: IdentityRole,
        status: Status,
        lan: String
    ): Result<BlogTranslation>? {
        if (status.platform.protocol.notBluesky) return null
        return Result.failure(RuntimeException("Not implemented"))
    }
}
