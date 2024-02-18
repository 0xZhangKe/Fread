package com.zhangke.utopia.rss.internal.repo

import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.utopia.rss.internal.adapter.RssStatusAdapter
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class RssStatusRepo @Inject constructor(
    private val rssStatusAdapter: RssStatusAdapter,
    private val channelRepo: RssChannelRepo,
    private val itemRepo: RssItemRepo,
) {

    suspend fun getStatus(
        uriInsight: RssUriInsight,
        limit: Int,
        maxId: String? = null,
        sinceId: String? = null,
    ): Result<List<Status>> {
        val channelResult = channelRepo.getChannelByUrl(uriInsight.url)
        if (channelResult.isFailure) {
            return Result.failure(channelResult.exceptionOrThrow())
        }
        val channel = channelResult.getOrThrow()
        var allItems = itemRepo.queryItemsBySourceUrl(uriInsight.url)
        if (maxId.isNullOrEmpty().not()) {
            val maxIndex = allItems.indexOfFirst { it.id == maxId }
            if (maxIndex < 0 || maxIndex >= allItems.lastIndex) {
                return Result.success(emptyList())
            } else {
                allItems = allItems.subList(maxIndex + 1, allItems.size)
            }
        }
        if (sinceId.isNullOrEmpty()) {
            val sinceIndex = allItems.indexOfFirst { it.id == sinceId }
            if (sinceIndex <= 0) {
                return Result.success(emptyList())
            } else {
                allItems = allItems.subList(0, sinceIndex)
            }
        }
        return allItems
            .take(limit)
            .map { rssStatusAdapter.toStatus(uriInsight, channel, it) }
            .let { Result.success(it) }
    }

    suspend fun checkIsFirstStatus(uriInsight: RssUriInsight, status: Status): Result<Boolean> {
        var items = itemRepo.queryItemsBySourceUrl(uriInsight.url)
        if (items.isEmpty()) {
            val itemsResult = channelRepo.getChannelByUrl(uriInsight.url)
            if (itemsResult.isFailure) {
                return Result.failure(itemsResult.exceptionOrThrow())
            }
            items = itemsResult.getOrThrow().items
        }
        val index = items.indexOfFirst { it.id == status.id }
        return Result.success(index == items.lastIndex)
    }
}
