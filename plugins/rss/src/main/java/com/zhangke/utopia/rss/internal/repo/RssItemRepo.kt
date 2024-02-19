package com.zhangke.utopia.rss.internal.repo

import android.graphics.Rect
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.db.RssItemDao
import com.zhangke.utopia.rss.internal.db.RssItemEntity
import com.zhangke.utopia.rss.internal.rss.RssItem
import javax.inject.Inject

class RssItemRepo @Inject constructor(
    rssDatabases: RssDatabases,
) {

    private val itemDao: RssItemDao = rssDatabases.getRssItemDao()

    suspend fun queryItemsBySourceUrl(
        rssSourceUrl: String,
    ): List<RssItem> {
        return itemDao.queryBySourceUrl(rssSourceUrl)
            .map { it.toItem() }
    }

    suspend fun insertItems(rssSourceUrl: String, items: List<RssItem>) {
        itemDao.insertList(items.map { it.toEntity(rssSourceUrl) })
    }

    suspend fun deleteById(id: String) {
        itemDao.deleteById(id)
    }

    suspend fun deleteBySourceUrl(rssSourceUrl: String) {
        itemDao.deleteBySourceUrl(rssSourceUrl)
    }

    private fun RssItem.toEntity(url: String): RssItemEntity {
        return RssItemEntity(
            id = this.id,
            rssSourceUrl = url,
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

    private fun RssItemEntity.toItem() = RssItem(
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
