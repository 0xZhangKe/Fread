package com.zhangke.fread.activitypub.app.internal.screen.instance

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes

object PlatformDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/platform/detail"

    private const val PARAM_BASE_URL = "baseUrl"

    fun buildRoute(baseUrl: FormalBaseUrl): String {
        return "$ROUTE?$PARAM_BASE_URL=$baseUrl"
    }

    fun parseParams(route: String): FormalBaseUrl {
        val queries = SimpleUri.parse(route)!!.queries
        return queries[PARAM_BASE_URL]!!.let(FormalBaseUrl::parse)!!
    }
}
