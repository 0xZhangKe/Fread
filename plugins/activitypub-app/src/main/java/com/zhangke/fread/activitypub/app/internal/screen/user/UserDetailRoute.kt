package com.zhangke.fread.activitypub.app.internal.screen.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.encodeToUrlString
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.fread.status.uri.encode

object UserDetailRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/detail"

    const val PARAMS_ROLE = "role"
    const val PARAMS_USER_URI = "userUri"
    const val PARAMS_WEB_FINGER = "webFinger"

    fun buildRoute(role: IdentityRole, userUri: FormalUri): String {
        return "$ROUTE?$PARAMS_USER_URI=${userUri.encode()}&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }

    fun buildRoute(role: IdentityRole, webFinger: WebFinger): String {
        return "$ROUTE?$PARAMS_WEB_FINGER=${webFinger.encodeToUrlString()}&$PARAMS_ROLE=${role.encodeToUrlString()}"
    }
}
