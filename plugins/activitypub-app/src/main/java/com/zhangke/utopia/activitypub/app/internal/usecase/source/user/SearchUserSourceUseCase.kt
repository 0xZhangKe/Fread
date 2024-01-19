package com.zhangke.utopia.activitypub.app.internal.usecase.source.user

import android.net.Uri
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class SearchUserSourceUseCase @Inject constructor(
    private val baseUrlManager: BaseUrlManager,
    private val userRepo: UserRepo,
    private val userUriTransformer: UserUriTransformer,
) {

    suspend operator fun invoke(query: String): Result<StatusSource?> {
        searchAsUserUri(query).getOrNull()?.let { return Result.success(it) }
        searchAsWebFinger(query).getOrNull()?.let { return Result.success(it) }
        return searchAsUrl(query)
    }

    private suspend fun searchAsUserUri(query: String): Result<StatusSource?> {
        FormalUri.from(query)
            ?.let(userUriTransformer::parse)
            ?.let {
                userRepo.getUserSource(
                    baseUrl = baseUrlManager.decideBaseUrl(it.baseUrl),
                    userUriInsights = it,
                ).getOrNull()
            }?.let {
                return Result.success(it)
            }
        return Result.success(null)
    }

    private suspend fun searchAsWebFinger(query: String): Result<StatusSource?> {
        val webFinger = WebFinger.create(query) ?: return Result.success(null)
        return userRepo.lookupUserSource(
            baseUrl = baseUrlManager.decideBaseUrl(),
            acct = webFinger.toString(),
        )
    }

    /**
     * https://m.cmx.im/@webb@androiddev.social
     * https://androiddev.social/@webb
     */
    private suspend fun searchAsUrl(query: String): Result<StatusSource?> {
        WebFinger.create(query) ?: return Result.success(null)
        val uri = Uri.parse(query)
        val scheme = if (uri.scheme.isNullOrEmpty()) HttpScheme.HTTPS else uri.scheme!!
        if (!HttpScheme.validate(scheme)) return Result.success(null)
        val host = uri.host
        if (host.isNullOrEmpty()) return Result.success(null)
        val baseUrl = FormalBaseUrl.build(scheme, host)
        val acct = uri.path
            ?.removePrefix("/")
            ?.removeSuffix("/")
        if (acct.isNullOrEmpty()) return Result.success(null)
        return userRepo.lookupUserSource(
            baseUrl = baseUrl,
            acct = acct,
        )
    }
}
