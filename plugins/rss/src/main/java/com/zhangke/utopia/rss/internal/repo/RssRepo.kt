package com.zhangke.utopia.rss.internal.repo

import com.zhangke.utopia.rss.internal.db.RssChannelEntity
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.db.RssItemEntity
import com.zhangke.utopia.rss.internal.model.RssChannelItem
import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.rss.RssFetcher
import com.zhangke.utopia.rss.internal.rss.RssItem
import javax.inject.Inject

class RssRepo @Inject constructor(
    rssDatabases: RssDatabases
) {

    private val channelDao = rssDatabases.getRssChannelDao()
    private val itemDao = rssDatabases.getRssItemDao()

    suspend fun getRssSource(
        url: String,
        forceRemote: Boolean,
    ): Result<RssSource?> {
        if (!forceRemote) {
            channelDao.queryByUrl(url)?.toRssSource()?.let { return Result.success(it) }
        }
        return RssFetcher.fetchRss(url)
            .onSuccess { (source, items) ->
                itemDao.insertList(items.map { it.toEntity(url) })
                insertSource(source)
            }
            .map { (source, _) ->
                source
            }
    }

    suspend fun getRssItems(
        url: String,
        limit: Int,
        maxId: String? = null,
        sinceId: String? = null,
    ): Result<List<RssChannelItem>> {
        var allItems = itemDao.queryBySourceUrl(url)
        if (maxId.isNullOrEmpty().not()){
            val maxIndex = allItems.indexOfFirst { it.id == maxId }
            if (maxIndex < 0 || maxIndex >= allItems.lastIndex) {
                return Result.success(emptyList())
            } else {
                allItems = allItems.subList(maxIndex + 1, allItems.size)
            }
        }
        if (sinceId.isNullOrEmpty().not()) {
            val sinceIndex = allItems.indexOfFirst { it.id == sinceId }
            if (sinceIndex > 0){
                allItems = allItems.subList(0, sinceIndex)
            }
            if (allItems.size >= limit){
                return Result.success(allItems.map { it.toChannelItem() })
            }
            RssFetcher.fetchRss(url)
        }
        if (allItems.size < limit){

        }
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

    private fun RssItemEntity.toChannelItem(): RssChannelItem{
        return RssChannelItem(
            id = this.id,
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
            sourceUrl = this.rssSourceUrl,
            categories = this.categories,
            commentsUrl = this.commentsUrl,
        )
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
