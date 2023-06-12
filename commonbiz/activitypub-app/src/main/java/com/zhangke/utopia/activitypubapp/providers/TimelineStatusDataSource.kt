package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.framework.feeds.fetcher.LoadParams
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.framework.feeds.fetcher.StatusSourceData
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType
import com.zhangke.utopia.status.Status

class TimelineStatusDataSource(
    private val host: String,
    private val type: TimelineSourceType,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) : StatusDataSource<String, Status> {

    override suspend fun load(
        params: LoadParams<String>
    ): Result<StatusSourceData<String, Status>> {
        val client = obtainActivityPubClientUseCase(host)
        val timelineRepo = client.timelinesRepo

        val timelineListResult = when (type) {
            TimelineSourceType.PUBLIC -> timelineRepo.publicTimelines(
                minId = params.pageKey,
                limit = params.loadSize,
            )

            TimelineSourceType.LOCAL -> timelineRepo.localTimelines(
                minId = params.pageKey,
                limit = params.loadSize,
            )

            TimelineSourceType.HOME -> timelineRepo.homeTimeline(
                minId = params.pageKey,
                limit = params.loadSize,
            )
        }
        return timelineListResult.map {
            StatusSourceData(
                data = it.map { item -> activityPubStatusAdapter.adapt(item, host) },
                nextPageKey = it.lastOrNull()?.id,
            )
        }
    }

    override fun getRefreshKey(): String? {
        return null
    }
}
