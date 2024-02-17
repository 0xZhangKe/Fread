package com.zhangke.utopia.rss.internal.rss.parser

import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.rss.adapter.convert

object RssParser {

    private val rssParser = com.prof18.rssparser.RssParser()

    suspend fun parse(rssText: String): RssChannel? {
        return rssParser.parse(rssText).convert()
    }
}
