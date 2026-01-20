package com.zhangke.fread.rss.internal.rss

import com.prof18.rssparser.RssParser
import com.zhangke.fread.rss.internal.rss.adapter.convert

class RssParserWrapper(
    private val rssParser: RssParser,
) {
    suspend fun getRssChannel(url: String): RssChannel {
        return rssParser.getRssChannel(url).convert()
    }

    suspend fun parse(rssText: String): RssChannel {
        return rssParser.parse(rssText).convert()
    }
}
