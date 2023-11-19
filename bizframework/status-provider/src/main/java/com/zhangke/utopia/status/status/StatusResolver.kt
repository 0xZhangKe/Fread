package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.feeds.fetcher.FeedsFetcher
import com.zhangke.framework.feeds.fetcher.FeedsGenerator
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    fun getStatusDataSourceByUri(
        uris: List<String>
    ): List<StatusDataSource<*, Status>> {
        return uris.map { StatusProviderUri.from(it)!! }
            .mapNotNull { uri -> resolverList.mapFirstOrNull { it.getStatusDataSourceByUri(uri) } }
    }

    fun getStatusFeedsByUris(
        uris: List<String>,
        pageSize: Int,
    ): FeedsFetcher<Status> {
        return FeedsFetcher(getStatusDataSourceByUri(uris), pageSize, FeedsGenerator())
    }
}

interface IStatusResolver {

    fun getStatusDataSourceByUri(uri: StatusProviderUri): StatusDataSource<*, Status>?
}
