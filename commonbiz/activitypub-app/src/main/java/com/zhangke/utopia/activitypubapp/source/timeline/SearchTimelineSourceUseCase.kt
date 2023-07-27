package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypubapp.servers.GetActivityPubServerUseCase
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class SearchTimelineSourceUseCase @Inject constructor(
    private val getServerInstance: GetActivityPubServerUseCase,
    private val resolveTimelineSourceByUri: ResolveTimelineSourceByUriUseCase,
) {

    suspend operator fun invoke(query: String): Result<List<TimelineSource>> {
        StatusProviderUri.create(query)
            ?.let { resolveTimelineSourceByUri(it) }?.getOrNull()
            ?.let { source -> return@let listOf(source) }

        val url = ActivityPubUrl.create(query) ?: return Result.success(emptyList())
        val host = url.host
        val instance = getServerInstance(host).getOrNull()
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
