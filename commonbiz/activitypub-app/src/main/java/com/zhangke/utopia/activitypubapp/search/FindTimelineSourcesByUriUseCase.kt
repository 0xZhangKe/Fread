package com.zhangke.utopia.activitypubapp.search

import com.google.auto.service.AutoService
import com.zhangke.utopia.activitypubapp.domain.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.search.IFindSourceListByUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@AutoService(IFindSourceListByUriUseCase::class)
class FindTimelineSourcesByUriUseCase : IFindSourceListByUriUseCase {

    override suspend fun invoke(uri: String): Result<List<StatusSource>> {
        val url = ActivityPubUrl.create(uri) ?: return Result.success(emptyList())
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
