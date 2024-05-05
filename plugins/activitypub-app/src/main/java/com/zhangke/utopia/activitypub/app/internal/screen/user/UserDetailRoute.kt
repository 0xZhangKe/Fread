package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.decodeAsUri
import com.zhangke.framework.utils.encodeToUrlString
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.encodeToUrlString
import com.zhangke.utopia.status.uri.FormalUri
import com.zhangke.utopia.status.uri.encode

object UserDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/detail"

    private const val PARAMS_ROLE = "role"
    private const val PARAMS_USER_URI = "userUri"
    private const val PARAMS_WEB_FINGER = "webFinger"

    fun buildRoute(role: IdentityRole, userUri: FormalUri): String {
        return "$ROUTE?$PARAMS_USER_URI=${userUri.encode()}&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }

    fun buildRoute(role: IdentityRole, webFinger: WebFinger): String {
        return "$ROUTE?$PARAMS_WEB_FINGER=${webFinger.encodeToUrlString()}&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }

    fun parseRoute(route: String): Triple<IdentityRole, FormalUri?, WebFinger?> {
        val queries = SimpleUri.parse(route)!!.queries
        val role = queries[PARAMS_ROLE]!!.let { IdentityRole.decodeFromString(it) }!!
        val userUri = queries[PARAMS_USER_URI]?.decodeAsUri()?.let { FormalUri.from(it) }
        val webFinger = queries[PARAMS_WEB_FINGER]?.let { WebFinger.decodeFromUrlString(it) }
        return Triple(role, userUri, webFinger)
    }
}
