package com.zhangke.utopia.rss.internal.repo

import com.zhangke.utopia.rss.internal.db.RssChannelDao
import com.zhangke.utopia.rss.internal.db.RssChannelEntity
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.rss.RssChannel
import javax.inject.Inject

class RssChannelRepo @Inject constructor(
    rssDatabases: RssDatabases,
) {

    private val channelDao: RssChannelDao = rssDatabases.getRssChannelDao()

    suspend fun getChannel(url: String): RssChannel {
        channelDao.queryByUrl(url)?.let {  }
    }

    private fun RssChannelEntity.toRssChannel(): RssChannel{

    }
}
