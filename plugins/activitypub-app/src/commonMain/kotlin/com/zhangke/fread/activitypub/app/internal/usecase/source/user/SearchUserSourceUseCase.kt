package com.zhangke.fread.activitypub.app.internal.usecase.source.user

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class SearchUserSourceUseCase @Inject constructor(
    private val userRepo: UserRepo,
    private val userUriTransformer: UserUriTransformer,
) {

    suspend operator fun invoke(role: IdentityRole, query: String): Result<StatusSource?> {
        searchAsUserUri(role, query).getOrNull()?.let { return Result.success(it) }
        searchAsWebFinger(role, query).getOrNull()?.let { return Result.success(it) }
        return searchAsUrl(role, query)
    }

    private suspend fun searchAsUserUri(role: IdentityRole, query: String): Result<StatusSource?> {
        FormalUri.from(query)
            ?.let(userUriTransformer::parse)
            ?.let {
                val finalRole = if (role.nonRole) {
                    IdentityRole(it.uri, null)
                } else {
                    role
                }
                userRepo.getUserSource(
                    role = finalRole,
                    userUriInsights = it,
                ).getOrNull()
            }?.let {
                return Result.success(it)
            }
        return Result.success(null)
    }

    private suspend fun searchAsWebFinger(
        role: IdentityRole,
        query: String,
    ): Result<StatusSource?> {
        val webFinger = WebFinger.create(query) ?: return Result.success(null)
        val finalRole = if (role.nonRole) {
            val baseUrl = FormalBaseUrl.parse(query) ?: return Result.success(null)
            IdentityRole(null, baseUrl = baseUrl)
        } else {
            role
        }
        return userRepo.lookupUserSource(
            role = finalRole,
            acct = webFinger.toString(),
        )
    }

    /**
     * https://m.cmx.im/@webb@androiddev.social
     * https://androiddev.social/@webb
     */
    private suspend fun searchAsUrl(role: IdentityRole, query: String): Result<StatusSource?> {
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
        val finalRole = if (role.nonRole) {
            val baseUrl = FormalBaseUrl.parse(query)
            IdentityRole(null, baseUrl)
        } else {
            role
        }
        return userRepo.lookupUserSource(
            role = finalRole,
            acct = acct,
        )
    }
}
