package com.zhangke.framework.feeds.fetcher

interface StatusDataSource<Key, Value: StatusData> {

    suspend fun load(params: LoadParams<Key>): Result<StatusSourceData<Key, Value>>

    fun getRefreshKey(): Key?
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

interface StatusData {

    val datetime: Long

    val authorId: String
}