package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import com.zhangke.framework.network.SimpleUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.encodeToUrlString
import java.net.URLDecoder
import java.net.URLEncoder

object HashtagTimelineRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/hashtag/timeline"

    private const val PARAMS_ROLE = "role"
    private const val PARAM_HASHTAG = "hashtag"

    fun buildRoute(role: IdentityRole, hashtag: String): String {
        val encodedHashtag = URLEncoder.encode(hashtag.removePrefix("#"), Charsets.UTF_8.name())
        return "$ROUTE?$PARAM_HASHTAG=$encodedHashtag&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }

    fun parseRoute(route: String): Pair<IdentityRole, String> {
        val queries = SimpleUri.parse(route)!!.queries
        val hashtag = queries[PARAM_HASHTAG]!!.let {
            URLDecoder.decode(it, Charsets.UTF_8.name())
        }
        val role = queries[PARAMS_ROLE]!!.let { IdentityRole.decodeFromString(it) }!!
        return role to hashtag
    }
}
