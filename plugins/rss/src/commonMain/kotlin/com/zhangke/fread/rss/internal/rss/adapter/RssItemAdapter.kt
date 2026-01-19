@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssItem
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

fun RssItem.convert(): com.zhangke.fread.rss.internal.rss.RssItem {
    return com.zhangke.fread.rss.internal.rss.RssItem(
        id = getRssItemId(this),
        title = this.title.ifNullOrEmpty { "Unknown" },
        author = this.author,
        link = this.link,
        pubDate = formatRssDate(this.pubDate),
        description = this.description,
        content = this.content,
        image = this.image,
        audio = this.audio,
        video = this.video,
        sourceName = this.sourceName,
        sourceUrl = this.sourceUrl,
        categories = this.categories,
        commentsUrl = this.commentsUrl,
    )
}

private fun getRssItemId(rssItem: RssItem): String {
    if (rssItem.guid.isNullOrEmpty().not()) {
        return rssItem.guid!!
    }
    return rssItem.link.ifNullOrEmpty {
        rssItem.title.ifNullOrEmpty { getCurrentTimeMillis().toString() }
    }
}

internal fun formatRssDate(datetime: String?): Instant {
    if (datetime.isNullOrEmpty()) return Clock.System.now()
    return DateParser.parseAll(datetime) ?: Clock.System.now()
}
