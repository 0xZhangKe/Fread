package com.zhangke.fread.activitypub.app.internal.screen.status.post

import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.encodeToUrlString
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.fread.status.uri.encode
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

object PostStatusScreenRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/status/post"

    private const val PARAM_ACCOUNT_URI = "accountUri"

    private const val PARAM_EDIT_BLOG = "editBlog"

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
        val encodedName = replyAuthorName.encodeAsUri()
        return buildString {
            append("$ROUTE?$PARAM_ACCOUNT_URI=${accountUri.encode()}")
            append("&$PARAM_REPLY_TO_BLOG_ACCT=${replyToBlogWebFinger.encodeToUrlString()}")
            append("&$PARAM_REPLY_TO_BLOG_ID=$replyToBlogId")
            append("&$PARAM_REPLY_TO_AUTHOR_NAME=$encodedName")
            append("&$PARAMS_REPLY_VISIBILITY=${replyVisibility.name}")
        }
    }

    fun buildEditBlogRoute(
        accountUri: FormalUri,
        blog: Blog,
    ): String {
        return buildString {
            append("$ROUTE?$PARAM_ACCOUNT_URI=${accountUri.encode()}")
            val blogString = Json.encodeToString(Blog.serializer(), blog)
            append("&$PARAM_EDIT_BLOG=${blogString.encodeAsUri()}}")
        }
    }

    fun parse(route: String): PostStatusScreenParams {
        val queries = SimpleUri.parse(route)!!.queries
        val accountUri = queries[PARAM_ACCOUNT_URI]?.decodeAsUri()?.let { FormalUri.from(it) }
        parseAsReply(accountUri, queries)?.let { return it }
        parseAsEdit(accountUri, queries)?.let { return it }
        return PostStatusScreenParams.PostStatusParams(accountUri)
    }

    private fun parseAsReply(
        accountUri: FormalUri?,
        queries: Map<String, String>,
    ): PostStatusScreenParams.ReplyStatusParams? {
        val replyWebFinger =
            queries[PARAM_REPLY_TO_BLOG_ACCT]?.let { WebFinger.decodeFromUrlString(it) }
        val replyToBlogId = queries[PARAM_REPLY_TO_BLOG_ID]
        val replyAuthorName = queries[PARAM_REPLY_TO_AUTHOR_NAME]?.decodeAsUri()
        val replyVisibility = queries[PARAMS_REPLY_VISIBILITY]?.let {
            StatusVisibility.valueOf(it)
        } ?: StatusVisibility.PUBLIC
        if (replyWebFinger != null && replyToBlogId != null && replyAuthorName != null) {
            return PostStatusScreenParams.ReplyStatusParams(
                accountUri = accountUri,
                replyToBlogWebFinger = replyWebFinger,
                replyToBlogId = replyToBlogId,
                replyAuthorName = replyAuthorName,
                replyVisibility = replyVisibility,
            )
        }
        return null
    }

    private fun parseAsEdit(
        accountUri: FormalUri?,
        queries: Map<String, String>,
    ): PostStatusScreenParams.EditStatusParams? {
        val blog = queries[PARAM_EDIT_BLOG]?.decodeAsUri()?.let {
            runCatching { Json.decodeFromString<Blog>(it) }.getOrNull()
        } ?: return null
        return PostStatusScreenParams.EditStatusParams(accountUri, blog)
    }

    private fun String.encodeAsUri(): String {
        return URLEncoder.encode(this, Charsets.UTF_8.name())
    }

    private fun String.decodeAsUri(): String {
        return URLDecoder.decode(this, Charsets.UTF_8.name())
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

    data class EditStatusParams(
        override val accountUri: FormalUri?,
        val blog: Blog,
    ) : PostStatusScreenParams
}
