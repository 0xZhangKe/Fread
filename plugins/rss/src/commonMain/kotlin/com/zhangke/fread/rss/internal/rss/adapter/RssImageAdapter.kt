package com.zhangke.fread.rss.internal.rss.adapter

import com.prof18.rssparser.model.RssImage

fun RssImage.convert(): com.zhangke.fread.rss.internal.rss.RssImage? {
    if (!this.isNotEmptyOrBlank()) return null
    return com.zhangke.fread.rss.internal.rss.RssImage(
        url = this.url!!,
        title = this.title,
        description = this.description,
    )
}

private fun RssImage.isNotEmptyOrBlank(): Boolean {
    return !url.isNullOrBlank() || !link.isNullOrBlank()
}
