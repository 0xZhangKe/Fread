package com.zhangke.fread.activitypub.app.internal.screen.user.tags

import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString

object TagListScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/tags"

    const val PARAM_ROLE = "role"

    fun buildRoute(role: IdentityRole): String {
        return buildString {
            append(ROUTE)
            append("?$PARAM_ROLE=${role.encodeToUrlString()}")
        }
    }
}
