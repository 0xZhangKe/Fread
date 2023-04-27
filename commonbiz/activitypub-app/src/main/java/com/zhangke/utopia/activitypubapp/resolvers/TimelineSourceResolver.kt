package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.source.timeline.TimelineRepo
import com.zhangke.utopia.activitypubapp.source.timeline.getServerAndType
import com.zhangke.utopia.activitypubapp.source.timeline.isTimelineSourceUri
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject

internal class TimelineSourceResolver @Inject constructor(
    private val repo: TimelineRepo,
) : IStatusSourceResolver {

    override fun applicable(uri: StatusSourceUri): Boolean {
        return uri.isTimelineSourceUri()
    }

    override suspend fun resolve(uri: StatusSourceUri): Result<StatusSource> {
        val (serverUrl, type) = uri.getServerAndType()
            ?: throw IllegalArgumentException("$uri is not a timeline source!")
        return repo.query(serverUrl.host, type)?.let { Result.success(it) } ?: Result.failure(
            IllegalArgumentException("Unresolved $uri")
        )
    }
}
