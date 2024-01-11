package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status

class TimelineDataSource(
    private val baseUrl: FormalBaseUrl,
    private val timelineSourceType: TimelineSourceType,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platform: BlogPlatform,
    private val getTimelineStatus: GetTimelineStatusUseCase,
) : PagingSource<String, Status>() {

    override fun getRefreshKey(state: PagingState<String, Status>): String = ""

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Status> {
        val offset = params.key
        val result = getTimelineStatus(
            serverBaseUrl = baseUrl,
            type = timelineSourceType,
            sinceId = params.key,
            limit = params.loadSize,
            maxId = null,
        ).map { list ->
            list.map {
                val supportActions = getStatusSupportAction(it)
                statusAdapter.toStatus(it, platform, supportActions)
            }
        }
        return if (result.isSuccess) {
            val resultList = result.getOrNull() ?: emptyList()
            LoadResult.Page(
                data = resultList,
                prevKey = null,
                nextKey = if (resultList.isEmpty()) null else resultList.size + offset,
            )
        } else {
            LoadResult.Error(result.exceptionOrNull() ?: IllegalStateException())
        }
    }
}
