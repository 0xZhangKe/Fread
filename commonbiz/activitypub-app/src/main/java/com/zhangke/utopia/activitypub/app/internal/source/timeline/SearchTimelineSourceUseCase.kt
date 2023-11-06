package com.zhangke.utopia.activitypub.app.internal.source.timeline

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class SearchTimelineSourceUseCase @Inject constructor(
    private val getServerInstance: GetActivityPubPlatformUseCase,
    private val resolveTimelineSourceByUri: ResolveTimelineSourceByUriUseCase,
) {

    suspend operator fun invoke(query: String): Result<List<TimelineSource>> {
        StatusProviderUri.create(query)
            ?.let { resolveTimelineSourceByUri(it) }?.getOrNull()
            ?.let { source -> return@let listOf(source) }

        val url = ActivityPubUrl.create(query) ?: return Result.success(emptyList())
        val host = url.host
        val server = getServerInstance(host).getOrNull()
            ?: return Result.success(emptyList())
        val resultList = listOf(
            buildTimelineSearchResult(server.name, host, TimelineSourceType.HOME),
            buildTimelineSearchResult(server.name, host, TimelineSourceType.LOCAL),
            buildTimelineSearchResult(server.name, host, TimelineSourceType.PUBLIC),
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
