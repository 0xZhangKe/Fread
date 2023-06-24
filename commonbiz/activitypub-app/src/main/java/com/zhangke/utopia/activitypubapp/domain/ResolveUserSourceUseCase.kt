package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class ResolveUserSourceUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val userSourceAdapter: ActivityPubAccountAdapter,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
) {

    suspend operator fun invoke(query: String): Result<UserSource?> {
        var webFinger = StatusProviderUri.create(query)
            ?.let { parseUriToUserUriUseCase(it)?.finger }
        if (webFinger == null) {
            webFinger = WebFinger.create(query)
        }
        webFinger ?: return Result.success(null)
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { userSourceAdapter.createSource(it) }
    }
}
