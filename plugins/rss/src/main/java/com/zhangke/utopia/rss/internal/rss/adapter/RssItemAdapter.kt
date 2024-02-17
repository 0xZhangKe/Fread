package com.zhangke.utopia.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssItem

fun RssItem.convert(): com.zhangke.utopia.rss.internal.rss.RssItem{
    return com.zhangke.utopia.rss.internal.rss.RssItem(
        guid = this.guid,
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
        sourceUrl = this.sourceUrl,
        categories = this.categories,
        itunesItemData = this.itunesItemData,
        commentsUrl = this.commentsUrl,
    )
}
