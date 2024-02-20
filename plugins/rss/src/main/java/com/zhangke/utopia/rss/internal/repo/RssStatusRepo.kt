package com.zhangke.utopia.rss.internal.repo

import com.zhangke.utopia.rss.internal.adapter.RssStatusAdapter
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class RssStatusRepo @Inject constructor(
    private val rssStatusAdapter: RssStatusAdapter,
    private val rssRepo: RssRepo,
) {

    suspend fun getStatus(
        uriInsight: RssUriInsight,
    ): Result<List<Status>> {
        return rssRepo.getRssItems(uriInsight.url)
            .map { (source, items) ->
                items.map { rssStatusAdapter.toStatus(uriInsight, source, it) }
            }
    }

    suspend fun checkIsFirstStatus(uriInsight: RssUriInsight, status: Status): Result<Boolean> {
        return getStatus(uriInsight)
            .map { statusList ->
                val index = statusList.indexOfFirst { it.id == status.id }
                index < 0 || index == statusList.lastIndex
            }
    }
}
