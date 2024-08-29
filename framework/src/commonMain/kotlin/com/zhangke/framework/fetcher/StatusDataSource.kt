package com.zhangke.framework.fetcher

interface StatusDataSource<Key, Value> {

    suspend fun load(params: LoadParams<Key>): Result<StatusSourceData<Key, Value>>

    fun getRefreshKey(): Key

    fun getDataId(data: Value): String

    fun getAuthId(data: Value): String

    fun getDatetime(data: Value): Long
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
