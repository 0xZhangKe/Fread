package com.zhangke.framework.feeds.fetcher

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FeedsFetcher<Key, Value: StatusData>(
    sourceList: List<StatusDataSource<Key, Value>>,
    private val pageSize: Int,
) {

    private val _dataFlow = MutableSharedFlow<List<Value>>(1)
    val dataFlow: Flow<List<Value>> = _dataFlow.asSharedFlow()

    private val pagingList = sourceList.map { StatusPagingSource(it, pageSize) }

    private val feedsGenerator = FeedsGenerator<Value>()

    suspend fun refresh() {
        _dataFlow.emit(emptyList())
        pagingList.forEach { it.refresh() }
    }

    suspend fun loadNextPage() {
        pagingList.map { it.loadNextPage() }
    }
}
