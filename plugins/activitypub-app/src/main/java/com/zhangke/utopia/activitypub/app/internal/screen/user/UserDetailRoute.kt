package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.framework.network.SimpleUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.utopia.status.uri.FormalUri
import java.net.URLDecoder
import java.net.URLEncoder

object UserDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/detail"

    private const val PARAMS_USER_URI = "userUri"

    fun buildRoute(userUri: FormalUri): String {
        return "$ROUTE?$PARAMS_USER_URI=${URLEncoder.encode(userUri.toRawString(), "UTF-8")}"
    }

    fun parseRoute(route: String): FormalUri {
        return SimpleUri.parse(route)!!.queries[PARAMS_USER_URI]!!
            .let { URLDecoder.decode(it, "UTF-8") }
            .let { FormalUri.from(it) }!!
    }
}
