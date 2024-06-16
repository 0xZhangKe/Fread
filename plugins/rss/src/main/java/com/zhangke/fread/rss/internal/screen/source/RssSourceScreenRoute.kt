package com.zhangke.fread.rss.internal.screen.source

import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.rss.internal.screen.RssRoutes
import java.net.URLDecoder
import java.net.URLEncoder

object RssSourceScreenRoute {

    const val ROUTE = "${RssRoutes.ROOT}/source"

    private const val PARAMS_URL = "url"

    fun buildRoute(url: String): String {
        val encodedUrl = URLEncoder.encode(url, Charsets.UTF_8.name())
        return "$ROUTE?$PARAMS_URL=$encodedUrl"
    }

    fun parseRoute(route: String): String {
        val queries = SimpleUri.parse(route)!!.queries
        return queries[PARAMS_URL]!!.let {
            URLDecoder.decode(it, Charsets.UTF_8.name())
        }
    }
}
