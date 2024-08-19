package com.zhangke.fread.activitypub.app.internal.screen.user.status

import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString

object StatusListScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/status"

    private const val PARAM_ROLE = "role"
    private const val PARAM_TYPE = "type"

    fun buildRoute(role: IdentityRole, type: StatusListType): String {
        return buildString {
            append(ROUTE)
            append("?$PARAM_TYPE=${type.name}")
            append("&$PARAM_ROLE=${role.encodeToUrlString()}")
        }
    }

    fun parse(route: String): Pair<IdentityRole, StatusListType>? {
        val queries = SimpleUri.parse(route)?.queries ?: return null
        val role = queries[PARAM_ROLE]?.let { IdentityRole.decodeFromString(it) }
        val type = queries[PARAM_TYPE]?.let { StatusListType.valueOf(it) }
        if (role == null || type == null) {
            return null
        }
        return Pair(role, type)
    }
}
