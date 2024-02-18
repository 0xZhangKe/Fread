package com.zhangke.utopia.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssChannel
import com.zhangke.framework.ktx.ifNullOrEmpty

fun RssChannel.convert(): com.zhangke.utopia.rss.internal.rss.RssChannel {
    return com.zhangke.utopia.rss.internal.rss.RssChannel(
        title = this.title.ifNullOrEmpty { "Unknown" },
        link = this.link,
        description = this.description,
        lastBuildDate = this.lastBuildDate?.let(::formatRssDate),
        updatePeriod = this.updatePeriod,
        image = this.image?.convert(),
        items = this.items.map { it.convert() }.sortedByDescending { it.pubDate.time },
    )
}
