package com.zhangke.utopia.activitypub.app.internal.usecase.source.timeline

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSource
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ResolveTimelineSourceByUriUseCase @Inject constructor(
    private val getServerInstance: GetActivityPubPlatformUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    suspend operator fun invoke(uri: StatusProviderUri): Result<TimelineSource?> {
        val timelineUri = parseUriToTimelineUriUseCase(uri) ?: return Result.success(null)
        val instance = getServerInstance(timelineUri.timelineServerHost).getOrNull()
            ?: return Result.success(null)
        return Result.success(
            buildTimelineSearchResult(
                instance.name,
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
