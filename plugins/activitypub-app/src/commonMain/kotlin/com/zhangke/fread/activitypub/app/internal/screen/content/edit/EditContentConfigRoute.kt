package com.zhangke.fread.activitypub.app.internal.screen.content.edit

import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes

object EditContentConfigRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/content/config/edit"

    const val PARAM_CONTENT_ID = "contentId"

    fun buildRoute(contentId: String): String {
        return "$ROUTE?$PARAM_CONTENT_ID=$contentId"
    }
}
