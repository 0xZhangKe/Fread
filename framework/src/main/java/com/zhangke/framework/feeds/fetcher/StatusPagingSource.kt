package com.zhangke.framework.feeds.fetcher

class StatusPagingSource<Key, Value: StatusData>(
    private val source: StatusDataSource<Key, Value>,
    private val pageSize: Int,
) {

    private var nextPageParams: LoadParams<Key>? = null

    fun refresh() {
        nextPageParams = null
    }

    suspend fun loadNextPage(): Result<List<Value>> {
        return source.load(nextPageParams ?: firstPageParams())
            .onSuccess {
                nextPageParams = it.nextLoadParams()
            }.map { it.data }
    }

    private fun firstPageParams(): LoadParams<Key> {
        return LoadParams(pageKey = source.getRefreshKey(), loadSize = pageSize)
    }

    private fun StatusSourceData<Key, Value>.nextLoadParams(): LoadParams<Key> {
        return LoadParams(nextPageKey, pageSize, extra)
    }
}
