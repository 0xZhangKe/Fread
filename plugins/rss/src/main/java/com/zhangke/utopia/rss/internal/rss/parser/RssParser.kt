package com.zhangke.utopia.rss.internal.rss.parser

import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.rss.adapter.convert

object RssParser {

    private val rssParser = com.prof18.rssparser.RssParserBuilder(
        callFactory = GlobalOkHttpClient.client,
    ).build()

    suspend fun getRssChannel(url: String): RssChannel {
        return rssParser.getRssChannel(url).convert()
    }

    suspend fun parse(rssText: String): RssChannel {
        return rssParser.parse(rssText).convert()
    }
}
