package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSource
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class ResolveTimelineSourceUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    suspend operator fun invoke(uri: StatusProviderUri): Result<TimelineSource?> {
        val timelineUri = parseUriToTimelineUriUseCase(uri) ?: return Result.success(null)
        val client = obtainActivityPubClientUseCase(timelineUri.timelineServerHost)
        val instance = client.instanceRepo.getInstanceInformation().getOrNull()
            ?: return Result.success(null)
        return Result.success(
            buildTimelineSearchResult(
                instance.title,
                timelineUri.timelineServerHost,
                timelineUri.type
            )
        )
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
