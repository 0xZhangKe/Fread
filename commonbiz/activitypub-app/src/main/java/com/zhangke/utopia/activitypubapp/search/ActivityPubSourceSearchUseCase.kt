package com.zhangke.utopia.activitypubapp.search

import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.collect
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.domain.ResolveUserSourceUseCase
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.search.ISearchStatusSourceUseCase
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@Filt
class ActivityPubSourceSearchUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val resolveUserSourceUseCase: ResolveUserSourceUseCase,
) : ISearchStatusSourceUseCase {

    override suspend fun invoke(query: String): Result<List<StatusSource>> {
        return listOf(
            searchUseSource(query),
            searchTimelineSource(query),
        ).collect()
    }

    private suspend fun searchUseSource(query: String): Result<List<StatusSource>> {
        return resolveUserSourceUseCase(query).map {
            if (it == null) emptyList() else listOf(it)
        }
    }

    private suspend fun searchTimelineSource(query: String): Result<List<StatusSource>> {
        val url = ActivityPubUrl.create(query) ?: return Result.success(emptyList())
        val host = url.host
        val client = obtainActivityPubClientUseCase(host)
        val instance = client.instanceRepo.getInstanceInformation().getOrNull()
            ?: return Result.success(emptyList())
        val resultList = listOf(
            buildTimelineSearchResult(instance.title, host, TimelineSourceType.HOME),
            buildTimelineSearchResult(instance.title, host, TimelineSourceType.LOCAL),
            buildTimelineSearchResult(instance.title, host, TimelineSourceType.PUBLIC),
        )
        return Result.success(resultList)
    }

    private fun buildTimelineSearchResult(
        title: String,
        host: String,
        type: TimelineSourceType,
    ): TimelineSource {
        return TimelineSource(
            type = type,
            host = host,
            description = appContext.getString(
                R.string.activity_pub_search_timeline_name,
                title,
                type.nickName,
            ),
        )
    }
}
