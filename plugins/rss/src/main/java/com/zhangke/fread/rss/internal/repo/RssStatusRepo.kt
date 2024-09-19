package com.zhangke.fread.rss.internal.repo

import com.zhangke.fread.rss.internal.adapter.RssStatusAdapter
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

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
}
