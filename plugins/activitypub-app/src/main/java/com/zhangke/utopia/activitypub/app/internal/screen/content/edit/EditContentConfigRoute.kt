package com.zhangke.utopia.activitypub.app.internal.screen.content.edit

import com.zhangke.framework.network.SimpleUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes

object EditContentConfigRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/content/config/edit"

    private const val PARAM_CONTENT_ID = "contentId"

    fun buildRoute(contentId: Long): String {
        return "$ROUTE?$PARAM_CONTENT_ID=$contentId"
    }

    fun parseRoute(route: String): Long {
        val queries = SimpleUri.parse(route)!!.queries
        return queries[PARAM_CONTENT_ID]!!.toLong()
    }
}
