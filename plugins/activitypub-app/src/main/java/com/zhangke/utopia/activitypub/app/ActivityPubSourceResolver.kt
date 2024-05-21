package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.source.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class ActivityPubSourceResolver @Inject constructor(
    private val userRepo: UserRepo,
    private val userUriTransformer: UserUriTransformer,
) : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(
        role: IdentityRole?,
        uri: FormalUri
    ): Result<StatusSource?> {
        val userUriInsights = userUriTransformer.parse(uri) ?: return Result.success(null)
        val finalRole = role ?: IdentityRole(userUriInsights.uri, null)
        return userRepo.getUserSource(
            role = finalRole,
            userUriInsights = userUriInsights,
        )
    }

    override suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return emptyFlow()
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        return null
    }
}
