package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import com.zhangke.framework.network.SimpleUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes

object HashtagTimelineRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/hashtag/timeline"

    private const val PARAM_HASHTAG = "hashtag"

    fun buildRoute(hashtag: String): String {
        return "$ROUTE?$PARAM_HASHTAG=$hashtag"
    }

    fun parseRoute(route: String): String {
        val queries = SimpleUri.parse(route)!!.queries
        return queries[PARAM_HASHTAG]!!
    }
}
