package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.decodeAsUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.utopia.status.uri.FormalUri
import com.zhangke.utopia.status.uri.encode

object UserDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/detail"

    private const val PARAMS_USER_URI = "userUri"

    fun buildRoute(userUri: FormalUri): String {
        return "$ROUTE?$PARAMS_USER_URI=${userUri.encode()}"
    }

    fun parseRoute(route: String): FormalUri {
        return SimpleUri.parse(route)!!.queries[PARAMS_USER_URI]!!
            .decodeAsUri()
            .let { FormalUri.from(it) }!!
    }
}
