package com.zhangke.utopia.activitypub.app.internal.status

import com.zhangke.framework.feeds.fetcher.LoadParams
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.framework.feeds.fetcher.StatusSourceData
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.status.model.Status

class TimelineStatusDataSource(
    private val host: String,
    private val type: TimelineSourceType,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
) : StatusDataSource<String, Status> {

    override suspend fun load(
        params: LoadParams<String>
    ): Result<StatusSourceData<String, Status>> {
        if (params.pageKey == null) {
            return Result.success(StatusSourceData(data = emptyList(), nextPageKey = null))
        }
        val client = clientManager.getClient(host.toBaseUrl())
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
            val data = it.map { item -> activityPubStatusAdapter.adapt(item) }
            StatusSourceData(
                data = data,
                nextPageKey = data.lastOrNull()?.let(::getDataId),
            )
        }
    }

    override fun getRefreshKey(): String = ""

    override fun getDataId(data: Status): String = data.id

    override fun getAuthId(data: Status): String = data.authId

    override fun getDatetime(data: Status): Long = data.datetime
}
