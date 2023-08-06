package com.zhangke.framework.feeds.fetcher

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first

class FeedsFetcher<Value>(
    sourceList: List<StatusDataSource<*, Value>>,
    private val pageSize: Int,
    private val feedsGenerator: FeedsGenerator<Value> = FeedsGenerator()
) {

    private val _dataFlow = MutableSharedFlow<List<Value>>(1)

    val dataFlow: Flow<List<Value>> = _dataFlow.asSharedFlow()

    private val pagingList = sourceList.map { StatusPagingSource(it, pageSize) }
    private val pagingToStartId = mutableMapOf<StatusPagingSource<*, *>, String?>()

    init {
        _dataFlow.tryEmit(emptyList())
    }

    suspend fun refresh(): Result<Unit> {
        _dataFlow.emit(emptyList())
        pagingList.forEach { it.refresh() }
        pagingToStartId.clear()
        return loadNextPage()
    }

    suspend fun loadNextPage(): Result<Unit> {
        if (pagingList.isEmpty()) return Result.success(Unit)
        val sourceListResult = pagingList.map {
            it to it.loadNextPage(getSourceStartId(it))
        }
        if (sourceListResult.firstOrNull { it.second.isSuccess } == null) {
            return Result.failure(sourceListResult.first().second.exceptionOrNull()!!)
        }
        val params = sourceListResult.map {
            FeedsGenerator.GenerateParams(it.first, it.second.getOrThrow())
        }
        val resultSourceList = feedsGenerator.generate(params)
        resultSourceList.pagingToEndId.forEach { (key, value) ->
            setSourceStartId(key, value)
        }
        val currentSourceList = _dataFlow.first()
        val newList = mutableListOf<Value>()
        newList += currentSourceList
        newList += resultSourceList.list
        _dataFlow.emit(newList)
        return Result.success(Unit)
    }

    private fun getSourceStartId(source: StatusPagingSource<*, *>): String? {
        return pagingToStartId[source]
    }

    private fun setSourceStartId(source: StatusPagingSource<*, *>, startId: String?) {
        pagingToStartId[source] = startId
    }
}
