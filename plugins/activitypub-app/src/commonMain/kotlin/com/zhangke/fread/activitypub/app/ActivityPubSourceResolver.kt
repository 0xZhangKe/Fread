package com.zhangke.fread.activitypub.app

import com.zhangke.fread.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

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

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        return null
    }
}
