package com.zhangke.fread.activitypub.app

import com.zhangke.fread.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubSourceResolver @Inject constructor(
    private val userRepo: UserRepo,
    private val userUriTransformer: UserUriTransformer,
) : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        val userUriInsights = userUriTransformer.parse(uri) ?: return Result.success(null)
        val locator = PlatformLocator(baseUrl = userUriInsights.baseUrl)
        return userRepo.getUserSource(
            locator = locator,
            userUriInsights = userUriInsights,
        )
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        return null
    }
}
