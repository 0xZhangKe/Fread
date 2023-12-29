package com.zhangke.utopia.activitypub.app.internal.screen.server.trending

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status

class ServerTrendingDataSource(
    private val baseUrl: FormalBaseUrl,
    private val getServerTrending: GetServerTrendingUseCase,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platform: BlogPlatform,
) : PagingSource<Int, Status>() {

    override fun getRefreshKey(state: PagingState<Int, Status>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Status> {
        val offset = params.key ?: 0
        val result = getServerTrending(baseUrl = baseUrl, offset = offset, limit = params.loadSize)
            .map { list ->
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
