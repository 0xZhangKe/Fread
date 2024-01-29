package com.zhangke.utopia.activitypub.app.internal.screen.instance

import android.net.Uri
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes

object PlatformDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/platform/detail"

    private const val PARAM_BASE_URL = "baseUrl"

    fun buildRoute(baseUrl: FormalBaseUrl): String {
        return "$ROUTE?$PARAM_BASE_URL=$baseUrl"
    }

    fun parseBaseUrl(route: String): FormalBaseUrl {
        return Uri.parse(route).getQueryParameter(PARAM_BASE_URL)!!.let(FormalBaseUrl::parse)!!
    }
}
