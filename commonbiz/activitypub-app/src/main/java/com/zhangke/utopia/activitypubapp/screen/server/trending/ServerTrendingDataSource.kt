package com.zhangke.utopia.activitypubapp.screen.server.trending

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypubapp.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.status.status.Status

class ServerTrendingDataSource(
    private val host: String,
    private val getServerTrending: GetServerTrendingUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
) : PagingSource<Int, Status>() {

    override fun getRefreshKey(state: PagingState<Int, Status>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Status> {
        val offset = params.key ?: 0
        val result = getServerTrending(host = host, offset = offset, limit = params.loadSize)
            .map { list -> list.map { statusAdapter.adapt(it) } }
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
