package com.zhangke.framework.feeds.fetcher

import com.zhangke.framework.utils.throwInDebug

class StatusPagingSource<Key, Value>(
    private val source: StatusDataSource<Key, Value>,
    private val pageSize: Int,
) {

    private var nextPageParams: LoadParams<Key>? = null

    // internal for test
    internal var currentPage: MutableList<Value> = mutableListOf()

    fun refresh() {
        nextPageParams = null
        currentPage.clear()
    }

    suspend fun loadNextPage(startId: String?): Result<List<Value>> {
        return if (startId.isNullOrEmpty()) {
            loadNextPageFromServer()
                .onSuccess {
                    currentPage.clear()
                    currentPage += it
                }
        } else {
            loadNexPageInternal(startId)
        }
    }

    private suspend fun loadNexPageInternal(startId: String): Result<List<Value>> {
        val index = currentPage.indexOfFirst { source.getDataId(it) == startId }
        if (index == -1) {
            throwInDebug("Load next page status, can`t find start id!")
            return Result.success(emptyList())
        }
        val leftSize = currentPage.size - index - 1
        if (leftSize >= pageSize) {
            return Result.success(currentPage.subList(index + 1, currentPage.size))
        } else {
            val nextPageResult = loadNextPageFromServer()
            if (nextPageResult.isFailure) {
                return nextPageResult
            }
            val nextPageList = nextPageResult.getOrThrow()
            if (nextPageList.size >= pageSize) {
                currentPage.drop(index).let {
                    currentPage.clear()
                    currentPage += it
                }
            }
            currentPage += nextPageList
            val nowIndex = currentPage.indexOfFirst { startId == source.getDataId(it) }
            val returnList = currentPage.subList(
                nowIndex + 1,
                minOf(nowIndex + 1 + pageSize, currentPage.size),
            )
            return Result.success(returnList)
        }
    }

    private suspend fun loadNextPageFromServer(): Result<List<Value>> {
        return source.load(nextPageParams ?: firstPageParams())
            .onSuccess { nextPageParams = it.nextLoadParams() }
            .map { it.data }
    }

    private fun firstPageParams(): LoadParams<Key> {
        return LoadParams(pageKey = source.getRefreshKey(), loadSize = pageSize)
    }

    private fun StatusSourceData<Key, Value>.nextLoadParams(): LoadParams<Key> {
        return LoadParams(nextPageKey, pageSize, extra)
    }

    fun getDataId(value: Value): String {
        return source.getDataId(value)
    }

    fun getDatetime(value: Value): Long {
        return source.getDatetime(value)
    }

    fun getAuthId(value: Value): String {
        return source.getAuthId(value)
    }
}
