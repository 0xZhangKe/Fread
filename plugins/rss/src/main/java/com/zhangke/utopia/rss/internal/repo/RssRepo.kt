package com.zhangke.utopia.rss.internal.repo

import com.zhangke.utopia.rss.internal.db.RssChannelEntity
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.model.RssChannelItem
import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.rss.RssFetcher
import javax.inject.Inject

class RssRepo @Inject constructor(
    rssDatabases: RssDatabases
) {

    private val channelDao = rssDatabases.getRssChannelDao()

    suspend fun getRssSource(
        url: String,
        forceRemote: Boolean = false,
    ): Result<RssSource?> {
        if (!forceRemote) {
            channelDao.queryByUrl(url)?.toRssSource()?.let { return Result.success(it) }
        }
        return fetchRssChannelByUrl(url)
    }

    suspend fun updateSourceName(url: String, name: String) {
        val source = channelDao.queryByUrl(url)
        if (source != null) {
            channelDao.insert(source.copy(displayName = name))
        }
    }

    suspend fun getRssItems(
        url: String,
    ): Result<Pair<RssSource, List<RssChannelItem>>> {
        return fetchRssItemsByUrl(url)
    }

    private suspend fun fetchRssChannelByUrl(url: String): Result<RssSource> {
        return RssFetcher.fetchRss(url)
            .onSuccess { insertSource(it.first) }
            .map { it.first }
    }

    private suspend fun fetchRssItemsByUrl(
        url: String
    ): Result<Pair<RssSource, List<RssChannelItem>>> {
        return RssFetcher.fetchRss(url)
            .onSuccess { insertSource(it.first) }
    }

    private suspend fun insertSource(source: RssSource) {
        var insertSource = source
        val oldSource = channelDao.queryByUrl(source.url)
        if (oldSource != null) {
            insertSource = source.copy(
                displayName = oldSource.displayName,
                addDate = oldSource.addDate,
            )
        }
        channelDao.insert(insertSource.toEntity())
    }

    private fun RssSource.toEntity(): RssChannelEntity {
        return RssChannelEntity(
            url = this.url,
            homePage = this.homePage,
            title = this.title,
            description = this.description,
            lastBuildDate = this.lastUpdateDate,
            updatePeriod = this.updatePeriod,
            thumbnail = this.thumbnail,
            addDate = this.addDate,
            lastUpdateDate = this.lastUpdateDate,
            displayName = this.displayName,
        )
    }

    private fun RssChannelEntity.toRssSource(): RssSource {
        return RssSource(
            url = this.url,
            homePage = this.homePage,
            title = this.title,
            displayName = this.displayName,
            addDate = this.addDate,
            lastUpdateDate = this.lastUpdateDate,
            description = this.description,
            thumbnail = this.thumbnail,
            updatePeriod = this.updatePeriod,
        )
    }
}
