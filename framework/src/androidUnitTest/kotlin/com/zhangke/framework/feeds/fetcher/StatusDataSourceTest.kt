package com.zhangke.framework.feeds.fetcher

import com.zhangke.framework.fetcher.LoadParams
import com.zhangke.framework.fetcher.StatusDataSource
import com.zhangke.framework.fetcher.StatusSourceData

data class MockData(
    val dataId: String,
    val datetime: Long = System.currentTimeMillis()
)

class MockDataSource(
    private val endPage: Int = Int.MAX_VALUE,
) : StatusDataSource<Int, MockData> {

    override suspend fun load(
        params: LoadParams<Int>
    ): Result<StatusSourceData<Int, MockData>> {
        val page = params.pageKey ?: return Result.success(
            StatusSourceData(data = emptyList(), nextPageKey = null)
        )
        if (page > endPage) return Result.success(StatusSourceData(emptyList(), page))
        val list = mutableListOf<MockData>()
        val start = page * params.loadSize
        val loadSize = if (page == endPage) params.loadSize / 2 else params.loadSize
        repeat(loadSize) {
            list += MockData("${start + it}")
        }
        return Result.success(
            StatusSourceData(
                data = list,
                nextPageKey = if (page >= endPage) null else page + 1,
            )
        )
    }

    override fun getRefreshKey(): Int {
        return 0
    }

    override fun getDataId(data: MockData): String {
        return data.dataId
    }

    override fun getAuthId(data: MockData): String = "ZhangKe"

    override fun getDatetime(data: MockData): Long = data.datetime
}