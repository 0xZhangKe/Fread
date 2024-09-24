package com.zhangke.fread.rss.internal.screen.source

import com.zhangke.framework.utils.UrlEncoder
import com.zhangke.fread.rss.internal.screen.RssRoutes

object RssSourceScreenRoute {

    const val ROUTE = "${RssRoutes.ROOT}/source"

    const val PARAMS_URL = "url"

    fun buildRoute(url: String): String {
        val encodedUrl = UrlEncoder.encode(url)
        return "$ROUTE?$PARAMS_URL=$encodedUrl"
    }
}
