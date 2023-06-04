package com.zhangke.utopia.activitypubapp.domain

import com.google.auto.service.AutoService
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubInstanceOwnerAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.status.domain.IFetchOwnerFromSourceUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner

@AutoService(IFetchOwnerFromSourceUseCase::class)
class FetchActivityPubOwnerFromSourceUseCase : IFetchOwnerFromSourceUseCase {

    override suspend fun invoke(source: StatusSource): Result<StatusSourceOwner?> {
        val host = when (source) {
            is TimelineSource -> source.host
            is UserSource -> source.webFinger.host
            else -> null
        } ?: return Result.success(null)
        val client = ObtainActivityPubClientUseCase()(host)
        val instanceOwnerAdapter = ActivityPubInstanceOwnerAdapter()
        return client.instanceRepo.getInstanceInformation().map { instanceOwnerAdapter.adapt(it) }
    }
}
