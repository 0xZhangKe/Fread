package com.zhangke.utopia.rss.internal.repo

import com.zhangke.utopia.rss.internal.db.RssChannelDao
import com.zhangke.utopia.rss.internal.db.RssChannelEntity
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.rss.RssImage
import com.zhangke.utopia.rss.internal.rss.RssItem
import com.zhangke.utopia.rss.internal.rss.RssParser
import javax.inject.Inject

class RssChannelRepo @Inject constructor(
    rssDatabases: RssDatabases,
    private val itemRepo: RssItemRepo,
) {

    private val channelDao: RssChannelDao = rssDatabases.getRssChannelDao()

    /**
     * Get channel from local database or remote.
     * This function don't care the rss items has already loaded or not.
     * @param forceRemote If true, force to get channel from remote.
     */
    suspend fun getChannelByUrl(
        url: String,
        forceRemote: Boolean = false,
    ): Result<RssChannel> {
        return if (forceRemote) {
            getChannelFromRemote(url)
        } else {
            val localChannel = queryChannelByUrl(url)
            if (localChannel != null) {
                Result.success(localChannel)
            } else {
                getChannelFromRemote(url)
            }
        }
    }

    private suspend fun getChannelFromRemote(url: String): Result<RssChannel> {
        val channel = try {
            RssParser.getRssChannel(url)
        } catch (e: Throwable) {
            return Result.failure(e)
        }
        itemRepo.insertItems(url, channel.items)
        channelDao.insert(channel.toEntity(url))
        return Result.success(channel)
    }

    private suspend fun queryChannelByUrl(url: String): RssChannel? {
        val channelEntity = channelDao.queryByUrl(url) ?: return null
        val items = itemRepo.queryItemsBySourceUrl(url)
        return convertToRssChannel(channelEntity, items)
    }

    private fun convertToRssChannel(
        channelEntity: RssChannelEntity,
        items: List<RssItem>,
    ): RssChannel {
        val image = if (channelEntity.imageUrl.isNullOrBlank()) {
            null
        } else {
            RssImage(
                url = channelEntity.imageUrl,
                title = channelEntity.imageTitle,
                description = channelEntity.imageDescription,
            )
        }
        return RssChannel(
            title = channelEntity.title,
            description = channelEntity.description,
            link = channelEntity.url,
            image = image,
            items = items,
            lastBuildDate = channelEntity.lastBuildDate,
            updatePeriod = channelEntity.updatePeriod,
        )
    }

    private fun RssChannel.toEntity(url: String): RssChannelEntity {
        return RssChannelEntity(
            url = url,
            title = this.title,
            description = this.description.orEmpty(),
            lastBuildDate = this.lastBuildDate,
            updatePeriod = this.updatePeriod,
            imageTitle = this.image?.title,
            imageDescription = this.image?.description,
            imageUrl = this.image?.url,
        )
    }
}
