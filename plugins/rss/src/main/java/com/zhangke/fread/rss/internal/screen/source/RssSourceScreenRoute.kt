package com.zhangke.fread.rss.internal.screen.source

import com.zhangke.fread.rss.internal.screen.RssRoutes
import java.net.URLEncoder

object RssSourceScreenRoute {

    const val ROUTE = "${RssRoutes.ROOT}/source"

    const val PARAMS_URL = "url"

    fun buildRoute(url: String): String {
        val encodedUrl = URLEncoder.encode(url, Charsets.UTF_8.name())
        return "$ROUTE?$PARAMS_URL=$encodedUrl"
    }
}
