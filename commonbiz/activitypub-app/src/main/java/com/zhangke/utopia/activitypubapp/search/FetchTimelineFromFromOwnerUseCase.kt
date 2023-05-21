package com.zhangke.utopia.activitypubapp.search

import com.google.auto.service.AutoService
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.domain.IFetchSourceFromOwnerUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner

@AutoService(IFetchSourceFromOwnerUseCase::class)
class FetchTimelineFromFromOwnerUseCase : IFetchSourceFromOwnerUseCase {

    override suspend fun invoke(owner: StatusSourceOwner): Result<List<StatusSource>> {
        val url = ActivityPubUrl.create(owner.uri) ?: return Result.success(emptyList())
        val host = url.host
        val client = ObtainActivityPubClientUseCase()(host)
        client.instanceRepo.getInstanceInformation().getOrNull()
            ?: return Result.success(emptyList())
        val sourceList = listOf(
            TimelineSource(host, TimelineSourceType.HOME),
            TimelineSource(host, TimelineSourceType.LOCAL),
            TimelineSource(host, TimelineSourceType.PUBLIC),
        )
        return Result.success(sourceList)
    }
}
