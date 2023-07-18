package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypubapp.uri.timeline.TimelineUriValidateUseCase
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

@Filt
class TimelineSourceResolver @Inject constructor(
    private val repo: TimelineRepo,
    private val timelineUriValidateUseCase: TimelineUriValidateUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) : IStatusSourceResolver {

    override fun applicable(uri: StatusProviderUri): Boolean {
        return timelineUriValidateUseCase(uri)
    }

    override suspend fun resolve(uri: StatusProviderUri): Result<StatusSource> {
        val timelineUri = parseUriToTimelineUriUseCase(uri)
            ?: throw IllegalArgumentException("$uri is not a timeline source!")
        return repo.query(timelineUri.timelineServerHost, timelineUri.type)
            ?.let { Result.success(it) } ?: Result.failure(
            IllegalArgumentException("Unresolved $uri")
        )
    }
}
