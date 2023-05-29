package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.status.source.IStatusSourceCacher
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@Filt
class TimelineSourceCacher @Inject constructor(
    private val repo: TimelineRepo,
) : IStatusSourceCacher {

    override suspend fun cache(statusSource: StatusSource) {
        if (statusSource !is TimelineSource) return
        repo.save(statusSource)
    }
}