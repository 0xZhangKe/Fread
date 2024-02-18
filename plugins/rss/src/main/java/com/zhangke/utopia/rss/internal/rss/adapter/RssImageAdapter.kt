package com.zhangke.utopia.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssImage

fun RssImage.convert(): com.zhangke.utopia.rss.internal.rss.RssImage? {
    if (!this.isNotEmpty()) return null
    return com.zhangke.utopia.rss.internal.rss.RssImage(
        url = this.url!!,
        title = this.title,
        description = this.description,
    )
}
