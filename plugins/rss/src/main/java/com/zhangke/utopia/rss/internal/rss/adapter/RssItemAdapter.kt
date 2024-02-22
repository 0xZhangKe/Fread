package com.zhangke.utopia.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssItem
import com.zhangke.framework.ktx.ifNullOrEmpty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun RssItem.convert(): com.zhangke.utopia.rss.internal.rss.RssItem {
    return com.zhangke.utopia.rss.internal.rss.RssItem(
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

internal fun formatRssDate(datetime: String?): Date {
    if (datetime.isNullOrEmpty()) return Date()
    val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    return format.parse(datetime) ?: Date()
}

private fun getRssItemId(rssItem: RssItem): String {
    if (rssItem.guid.isNullOrEmpty().not()) {
        return rssItem.guid!!
    }
    return rssItem.link.ifNullOrEmpty {
        rssItem.title.ifNullOrEmpty { System.currentTimeMillis().toString() }
    }
}
