package com.zhangke.utopia.activitypub.app.internal.screen.instance

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.SimpleUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes

object PlatformDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/platform/detail"

    private const val PARAM_BASE_URL = "baseUrl"
    private const val PARAM_ADDABLE = "addable"

    fun buildRoute(
        baseUrl: FormalBaseUrl,
        addable: Boolean = false,
    ): String {
        return "$ROUTE?$PARAM_BASE_URL=$baseUrl&$PARAM_ADDABLE=$addable"
    }

    fun parseParams(route: String): Pair<FormalBaseUrl, Boolean> {
        val queries = SimpleUri.parse(route)!!.queries
        val baseUrl = queries[PARAM_BASE_URL]!!.let(FormalBaseUrl::parse)!!
        val addable = queries[PARAM_ADDABLE]?.toBoolean() ?: false
        return baseUrl to addable
    }
}
