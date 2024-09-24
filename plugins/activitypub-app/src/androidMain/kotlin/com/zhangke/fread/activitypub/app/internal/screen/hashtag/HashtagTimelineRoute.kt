package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString
import java.net.URLEncoder

object HashtagTimelineRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/hashtag/timeline"

    const val PARAMS_ROLE = "role"
    const val PARAM_HASHTAG = "hashtag"

    fun buildRoute(role: IdentityRole, hashtag: String): String {
        val encodedHashtag = URLEncoder.encode(hashtag.removePrefix("#"), Charsets.UTF_8.name())
        return "$ROUTE?$PARAM_HASHTAG=$encodedHashtag&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }
}
