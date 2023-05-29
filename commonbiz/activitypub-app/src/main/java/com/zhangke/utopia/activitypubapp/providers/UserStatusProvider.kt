package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.protocol.isUserSource
import com.zhangke.utopia.activitypubapp.protocol.parseInfo
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.Status
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject

@Filt
class UserStatusProvider @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) : IStatusProvider {

    override fun applicable(sourceUri: StatusProviderUri): Boolean {
        return sourceUri.isUserSource()
    }

    override suspend fun requestStatuses(sourceUri: StatusProviderUri): Result<List<Status>> {
        val (webFinger, userId) = sourceUri.parseInfo() ?: return Result.failure(
            IllegalArgumentException("$sourceUri is not a User source.")
        )
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .getStatuses(userId)
            .map { list ->
                list.map { activityPubStatusAdapter.adapt(it, client.application.host) }
            }
    }
}