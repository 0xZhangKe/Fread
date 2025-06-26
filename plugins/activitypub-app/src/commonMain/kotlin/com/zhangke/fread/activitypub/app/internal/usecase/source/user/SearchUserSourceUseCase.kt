package com.zhangke.fread.activitypub.app.internal.usecase.source.user

import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class SearchUserSourceUseCase @Inject constructor(
    private val userRepo: UserRepo,
    private val userUriTransformer: UserUriTransformer,
) {

    suspend operator fun invoke(locator: PlatformLocator, query: String): Result<StatusSource?> {
        searchAsUserUri(locator, query).getOrNull()?.let { return Result.success(it) }
        searchAsWebFinger(locator, query).getOrNull()?.let { return Result.success(it) }
        return searchAsUrl(locator, query)
    }

    private suspend fun searchAsUserUri(
        locator: PlatformLocator,
        query: String
    ): Result<StatusSource?> {
        FormalUri.from(query)
            ?.let(userUriTransformer::parse)
            ?.let {
                userRepo.getUserSource(
                    locator = locator,
                    userUriInsights = it,
                ).getOrNull()
            }?.let {
                return Result.success(it)
            }
        return Result.success(null)
    }

    private suspend fun searchAsWebFinger(
        locator: PlatformLocator,
        query: String,
    ): Result<StatusSource?> {
        val webFinger = WebFinger.create(query) ?: return Result.success(null)
        return userRepo.lookupUserSource(
            locator = locator,
            acct = webFinger.toString(),
        )
    }

    /**
     * https://m.cmx.im/@webb@androiddev.social
     * https://androiddev.social/@webb
     */
    private suspend fun searchAsUrl(
        locator: PlatformLocator,
        query: String
    ): Result<StatusSource?> {
        WebFinger.create(query) ?: return Result.success(null)
        // FIXME: fix no scheme query
        val uri = query.toPlatformUri()
        val scheme = if (uri.scheme.isNullOrEmpty()) HttpScheme.HTTPS else uri.scheme!!
        if (!HttpScheme.validate(scheme)) return Result.success(null)
        val host = uri.host
        if (host.isNullOrEmpty()) return Result.success(null)
        val acct = uri.path
            ?.removePrefix("/")
            ?.removeSuffix("/")
        if (acct.isNullOrEmpty()) return Result.success(null)
        return userRepo.lookupUserSource(
            locator = locator,
            acct = acct,
        )
    }
}
