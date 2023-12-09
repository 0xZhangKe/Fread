package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.feeds.fetcher.FeedsFetcher
import com.zhangke.framework.feeds.fetcher.FeedsGenerator
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    fun getStatusDataSourceByUri(
        uris: List<StatusProviderUri>
    ): List<StatusDataSource<*, Status>> {
        return uris.mapNotNull { uri ->
            resolverList.mapFirstOrNull {
                it.getStatusDataSourceByUri(uri)
            }
        }
    }

    fun getStatusFeedsByUris(
        uris: List<StatusProviderUri>,
        pageSize: Int,
    ): FeedsFetcher<Status> {
        return FeedsFetcher(getStatusDataSourceByUri(uris), pageSize, FeedsGenerator())
    }

    suspend fun getStatusList(
        uri: StatusProviderUri,
        pageSie: Int,
    ): Result<List<Status>> {
        for (statusResolver in resolverList) {
            statusResolver.getStatusList(uri, pageSie)?.let { return it }
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }
}

interface IStatusResolver {

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(uri: StatusProviderUri, limit: Int): Result<List<Status>>?

    fun getStatusDataSourceByUri(uri: StatusProviderUri): StatusDataSource<*, Status>?
}
