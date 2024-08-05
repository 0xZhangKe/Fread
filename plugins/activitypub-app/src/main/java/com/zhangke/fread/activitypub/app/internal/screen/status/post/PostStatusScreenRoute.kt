package com.zhangke.fread.activitypub.app.internal.screen.status.post

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.decodeAsUri
import com.zhangke.framework.utils.encodeToUrlString
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.fread.status.uri.encode
import java.net.URLDecoder
import java.net.URLEncoder

object PostStatusScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/status/post"

    private const val PARAM_ACCOUNT_URI = "accountUri"
    private const val PARAM_REPLY_TO_BLOG_ACCT = "replyToBlogAcct"
    private const val PARAM_REPLY_TO_BLOG_ID = "replyToBlogId"
    private const val PARAM_REPLY_TO_AUTHOR_NAME = "replyAuthorName"
    private const val PARAMS_REPLY_VISIBILITY = "replyVisibility"

    fun buildRoute(accountUri: FormalUri): String {
        return "$ROUTE?$PARAM_ACCOUNT_URI=${accountUri.encode()}"
    }

    fun buildRoute(
        accountUri: FormalUri,
        replyToBlogWebFinger: WebFinger,
        replyToBlogId: String,
        replyAuthorName: String,
        replyVisibility: StatusVisibility = StatusVisibility.PUBLIC,
    ): String {
        val encodedName = URLEncoder.encode(replyAuthorName, Charsets.UTF_8.name())
        return buildString {
            append("$ROUTE?$PARAM_ACCOUNT_URI=${accountUri.encode()}")
            append("&$PARAM_REPLY_TO_BLOG_ACCT=${replyToBlogWebFinger.encodeToUrlString()}")
            append("&$PARAM_REPLY_TO_BLOG_ID=$replyToBlogId")
            append("&$PARAM_REPLY_TO_AUTHOR_NAME=$encodedName")
            append("&$PARAMS_REPLY_VISIBILITY=${replyVisibility.name}")
        }
    }

    /**
     * @return first: accountUri, second: replyToBlogId, third: replyAuthorName
     */
    fun parse(route: String): PostStatusScreenParams {
        val queries = SimpleUri.parse(route)!!.queries
        val accountUri = queries[PARAM_ACCOUNT_URI]?.decodeAsUri()?.let { FormalUri.from(it) }
        val replyWebFinger =
            queries[PARAM_REPLY_TO_BLOG_ACCT]?.let { WebFinger.decodeFromUrlString(it) }
        val replyToBlogId = queries[PARAM_REPLY_TO_BLOG_ID]
        val replyAuthorName = queries[PARAM_REPLY_TO_AUTHOR_NAME]?.let {
            URLDecoder.decode(it, Charsets.UTF_8.name())
        }
        val replyVisibility = queries[PARAMS_REPLY_VISIBILITY]?.let {
            StatusVisibility.valueOf(it)
        } ?: StatusVisibility.PUBLIC
        return if (replyWebFinger == null || replyToBlogId == null || replyAuthorName == null) {
            PostStatusScreenParams.PostStatusParams(accountUri)
        } else {
            PostStatusScreenParams.ReplyStatusParams(
                accountUri = accountUri,
                replyToBlogWebFinger = replyWebFinger,
                replyToBlogId = replyToBlogId,
                replyAuthorName = replyAuthorName,
                replyVisibility = replyVisibility,
            )
        }
    }
}

sealed interface PostStatusScreenParams {

    val accountUri: FormalUri?

    data class PostStatusParams(override val accountUri: FormalUri?) : PostStatusScreenParams

    data class ReplyStatusParams(
        override val accountUri: FormalUri?,
        val replyToBlogWebFinger: WebFinger,
        val replyToBlogId: String,
        val replyAuthorName: String,
        val replyVisibility: StatusVisibility = StatusVisibility.PUBLIC,
    ) : PostStatusScreenParams
}
