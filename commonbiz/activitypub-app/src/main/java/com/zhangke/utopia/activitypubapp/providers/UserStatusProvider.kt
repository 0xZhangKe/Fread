package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.currentActivityPubClient
import com.zhangke.utopia.activitypubapp.domain.ResolveUserSourceByWebFingerUseCase
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.user.getUserWebFinger
import com.zhangke.utopia.activitypubapp.source.user.isUserSource
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.Status
import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject

internal class UserStatusProvider @Inject constructor(
    private val resolveUserSourceByWebFingerUseCase: ResolveUserSourceByWebFingerUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) : IStatusProvider {

    override fun applicable(sourceUri: StatusSourceUri): Boolean {
        return sourceUri.isUserSource()
    }

    override suspend fun requestStatuses(sourceUri: StatusSourceUri): Result<List<Status>> {
        val webFinger = sourceUri.getUserWebFinger() ?: return Result.failure(
            IllegalArgumentException("$sourceUri is not a User source.")
        )
        val userSource = resolveUserSourceByWebFingerUseCase(webFinger) ?: return Result.failure(
            IllegalArgumentException("$webFinger it not found!")
        )
        var client = currentActivityPubClient
        if (client == null) {
            client = obtainActivityPubClient(webFinger.host)
        }
        return client.accountRepo
            .getStatuses(userSource.userId)
            .map { list ->
                list.map { activityPubStatusAdapter.adapt(it, client.application.host) }
            }
    }
}