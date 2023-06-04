package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

class ResolveUserSourceUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val userSourceAdapter: ActivityPubAccountAdapter,
) {

    suspend operator fun invoke(query: String): Result<UserSource?> {
        val webFinger = WebFinger.create(query) ?: return Result.success(null)
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { userSourceAdapter.adapt(it) }
    }
}
