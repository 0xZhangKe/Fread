package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.status.status.model.Status

class TimelineDataSource(
    private val baseUrl: FormalBaseUrl,
    private val timelineSourceType: TimelineSourceType,
    private val getTimelineStatus: GetTimelineStatusUseCase,
) : PagingSource<String, Status>() {

    override fun getRefreshKey(state: PagingState<String, Status>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Status> {
        val result = getTimelineStatus(
            serverBaseUrl = baseUrl,
            type = timelineSourceType,
            sinceId = params.key,
            limit = params.loadSize,
            maxId = null,
        )
        return if (result.isSuccess) {
            val resultList = result.getOrNull() ?: emptyList()
            LoadResult.Page(
                data = resultList,
                prevKey = null,
                nextKey = resultList.lastOrNull()?.id,
            )
        } else {
            LoadResult.Error(result.exceptionOrNull() ?: IllegalStateException())
        }
    }
}
