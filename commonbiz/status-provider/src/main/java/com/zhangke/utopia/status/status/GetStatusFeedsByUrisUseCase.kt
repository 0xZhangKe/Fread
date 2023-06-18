package com.zhangke.utopia.status.status

import com.zhangke.framework.feeds.fetcher.FeedsFetcher
import com.zhangke.framework.feeds.fetcher.FeedsGenerator
import javax.inject.Inject

class GetStatusFeedsByUrisUseCase @Inject constructor(
    private val dataSourceUseCase: GetStatusDataSourceByUrisUseCase,
    private val generator: FeedsGenerator<Status>,
) {

    suspend operator fun invoke(
        uris: List<String>,
        pageSize: Int,
    ): FeedsFetcher<Status> {
        return FeedsFetcher(dataSourceUseCase(uris), pageSize, generator)
    }
}
