package com.zhangke.fread.activitypub.app.internal.screen.user.status

import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString

object StatusListScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/status"

    const val PARAM_ROLE = "role"
    const val PARAM_TYPE = "type"

    fun buildRoute(role: IdentityRole, type: StatusListType): String {
        return buildString {
            append(ROUTE)
            append("?$PARAM_TYPE=${type.name}")
            append("&$PARAM_ROLE=${role.encodeToUrlString()}")
        }
    }
}
