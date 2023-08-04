package com.zhangke.framework.feeds.fetcher

interface StatusDataSource<Key, Value> {

    suspend fun load(params: LoadParams<Key>): Result<StatusSourceData<Key, Value>>

    fun getRefreshKey(): Key

    fun getDataId(): String

    fun getAuthId(): String

    fun getDatetime(): Long
}

data class LoadParams<Key>(
    val pageKey: Key?,
    val loadSize: Int,
    val extra: Map<String, String> = emptyMap(),
)

data class StatusSourceData<Key, Value>(
    val data: List<Value>,
    val nextPageKey: Key?,
    val extra: Map<String, String> = emptyMap(),
)
