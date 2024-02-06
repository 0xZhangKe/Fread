package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import com.zhangke.framework.network.SimpleUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes
import java.net.URLDecoder
import java.net.URLEncoder

object HashtagTimelineRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/hashtag/timeline"

    private const val PARAM_HASHTAG = "hashtag"

    fun buildRoute(hashtag: String): String {
        val encodedHashtag = URLEncoder.encode(hashtag.removePrefix("#"), Charsets.UTF_8.name())
        return "$ROUTE?$PARAM_HASHTAG=$encodedHashtag"
    }

    fun parseRoute(route: String): String {
        val queries = SimpleUri.parse(route)!!.queries
        return queries[PARAM_HASHTAG]!!.let {
            URLDecoder.decode(it, Charsets.UTF_8.name())
        }
    }
}
