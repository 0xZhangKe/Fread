package com.zhangke.utopia.rss.internal.repo

import com.zhangke.utopia.rss.internal.db.RssChannelEntity
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.db.RssItemEntity
import com.zhangke.utopia.rss.internal.model.RssChannelItem
import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.rss.RssFetcher
import javax.inject.Inject

class RssRepo @Inject constructor(
    rssDatabases: RssDatabases
) {

    private val channelDao = rssDatabases.getRssChannelDao()
    private val itemDao = rssDatabases.getRssItemDao()

    suspend fun getRssSource(url: String): Result<RssSource?> {
        channelDao.queryByUrl(url)?.toRssSource()?.let { return Result.success(it) }
        return RssFetcher.fetchRss(url)
            .onSuccess { (source, items) ->
                itemDao.insertList(items.map { it.toEntity(url) })
                channelDao.insert(source.toEntity())
            }
            .map { (source, _) ->
                source
            }
    }

    private fun RssChannelItem.toEntity(sourceUrl: String): RssItemEntity {
        return RssItemEntity(
            id = this.id,
            rssSourceUrl = sourceUrl,
            title = this.title,
            author = this.author,
            link = this.link,
            pubDate = this.pubDate,
            description = this.description,
            content = this.content,
            image = this.image,
            audio = this.audio,
            video = this.video,
            sourceName = this.sourceName,
            categories = this.categories,
            commentsUrl = this.commentsUrl,
        )
    }

    private fun RssSource.toEntity(): RssChannelEntity {
        return RssChannelEntity(
            url = this.url,
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
            title = this.title,
            displayName = this.title,
            addDate = this.addDate,
            lastUpdateDate = this.lastUpdateDate,
            description = this.description,
            thumbnail = this.thumbnail,
            updatePeriod = this.updatePeriod,
        )
    }
}
