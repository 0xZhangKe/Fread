package com.zhangke.fread.activitypub.app.internal.screen.user.tags

import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString

object TagListScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/tags"

    private const val PARAM_ROLE = "role"

    fun buildRoute(role: IdentityRole): String {
        return buildString {
            append(ROUTE)
            append("?$PARAM_ROLE=${role.encodeToUrlString()}")
        }
    }

    fun parseRoute(route: String): IdentityRole? {
        val queries = SimpleUri.parse(route)!!.queries
        val role = queries[PARAM_ROLE]?.let { IdentityRole.decodeFromString(it) }
        return role
    }
}
