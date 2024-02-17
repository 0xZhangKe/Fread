package com.zhangke.utopia.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssChannel

fun RssChannel.convert(): com.zhangke.utopia.rss.internal.rss.RssChannel {
    return com.zhangke.utopia.rss.internal.rss.RssChannel(
        title = this.title,
        link = this.link,
        description = this.description,
        lastBuildDate = this.lastBuildDate,
        updatePeriod = this.updatePeriod,
        image = this.image?.convert(),
        items = this.items.map { it.convert() },
    )
}
