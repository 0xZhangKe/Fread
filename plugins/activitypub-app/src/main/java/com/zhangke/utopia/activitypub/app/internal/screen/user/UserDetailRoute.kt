package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.decodeAsUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.encodeToUrlString
import com.zhangke.utopia.status.uri.FormalUri
import com.zhangke.utopia.status.uri.encode

object UserDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/detail"

    private const val PARAMS_ROLE = "role"
    private const val PARAMS_USER_URI = "userUri"

    fun buildRoute(role: IdentityRole, userUri: FormalUri): String {
        return "$ROUTE?$PARAMS_USER_URI=${userUri.encode()}&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }

    fun parseRoute(route: String): Pair<IdentityRole, FormalUri> {
        val queries = SimpleUri.parse(route)!!.queries
        val userUri = queries[PARAMS_USER_URI]!!.decodeAsUri().let { FormalUri.from(it) }!!
        val role = queries[PARAMS_ROLE]!!.let { IdentityRole.decodeFromString(it) }!!
        return role to userUri
    }
}
