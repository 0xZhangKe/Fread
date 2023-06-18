package com.zhangke.framework.feeds.fetcher


data class MockData(
    override val dataId: String,
    override val datetime: Long = System.currentTimeMillis()
) : StatusData {

    override val authorId: String = "AuthId"
}

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
}