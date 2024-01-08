package com.zhangke.utopia.activitypub.app.internal.screen.status.post

import androidx.core.net.toUri
import com.zhangke.utopia.activitypub.app.internal.route.ActivityPubRoutes

object PostStatusScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/status/post"

    private const val PARAM_REPLY_TO_BLOG_ID = "replyToBlogId"
    private const val PARAM_REPLY_TO_AUTHOR_NAME = "replyAuthorName"

    fun buildRoute(replyToBlogId: String, replyAuthorName: String): String {
        return "$ROUTE?$PARAM_REPLY_TO_BLOG_ID=$replyToBlogId&$PARAM_REPLY_TO_AUTHOR_NAME=$replyAuthorName"
    }

    fun parseReplyToBlogId(route: String): String? {
        return route.toUri().getQueryParameter(PARAM_REPLY_TO_BLOG_ID)
    }

    fun parseReplyToAuthorName(route: String): String? {
        return route.toUri().getQueryParameter(PARAM_REPLY_TO_AUTHOR_NAME)
    }
}