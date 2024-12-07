package com.zhangke.fread.bluesky.internal.screen.add

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.encode
import com.zhangke.fread.bluesky.internal.screen.BlueskyRoutes

object AddBlueskyContentRoute {

    const val ROUTE = "${BlueskyRoutes.ROOT}/user/detail"

    const val PARAMS_BASE_URL = "baseUrl"

    fun buildRoute(baseUrl: FormalBaseUrl): String {
        return "${ROUTE}?$PARAMS_BASE_URL=${baseUrl.encode()}"
    }
}
