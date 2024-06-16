package com.zhangke.fread.activitypub.app.internal.screen.status.post

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.decodeAsUri
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.fread.status.uri.encode
import java.net.URLDecoder
import java.net.URLEncoder

object PostStatusScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/status/post"

    private const val PARAM_ACCOUNT_URI = "accountUri"
    private const val PARAM_REPLY_TO_BLOG_ID = "replyToBlogId"
    private const val PARAM_REPLY_TO_AUTHOR_NAME = "replyAuthorName"

    fun buildRoute(accountUri: FormalUri): String {
        return "$ROUTE?$PARAM_ACCOUNT_URI=${accountUri.encode()}"
    }

    fun buildRoute(
        accountUri: FormalUri,
        replyToBlogId: String,
        replyAuthorName: String,
    ): String {
        val encodedName = URLEncoder.encode(replyAuthorName, Charsets.UTF_8.name())
        return buildString {
            append("$ROUTE?$PARAM_ACCOUNT_URI=${accountUri.encode()}")
            append("&$PARAM_REPLY_TO_BLOG_ID=$replyToBlogId")
            append("&$PARAM_REPLY_TO_AUTHOR_NAME=$encodedName")
        }
    }

    /**
     * @return first: accountUri, second: replyToBlogId, third: replyAuthorName
     */
    fun parse(route: String): Triple<FormalUri?, String?, String?> {
        val queries = SimpleUri.parse(route)!!.queries
        val accountUri = queries[PARAM_ACCOUNT_URI]?.decodeAsUri()?.let { FormalUri.from(it) }
        val replyToBlogId = queries[PARAM_REPLY_TO_BLOG_ID]
        val replyAuthorName = queries[PARAM_REPLY_TO_AUTHOR_NAME]?.let {
            URLDecoder.decode(it, Charsets.UTF_8.name())
        }
        return Triple(accountUri, replyToBlogId, replyAuthorName)
    }
}
