package com.zhangke.fread.activitypub.app.internal.screen.status.post

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

    const val PARAM_ACCOUNT_URI = "accountUri"

    const val PARAM_EDIT_BLOG = "editBlog"

    const val PARAM_REPLY_TO_BLOG_ACCT = "replyToBlogAcct"
    const val PARAM_REPLY_TO_BLOG_ID = "replyToBlogId"
    const val PARAM_REPLY_TO_AUTHOR_NAME = "replyAuthorName"
    const val PARAMS_REPLY_VISIBILITY = "replyVisibility"

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
            append("&$PARAM_EDIT_BLOG=${blogString.encodeAsUri()}")
        }
    }

    fun buildParams(
        accountUri: String?,
        editBlog: String?,
        replyBlogAcct: String?,
        replyBlogId: String?,
        replyAuthorName: String?,
        replyVisibility: String?,
    ): PostStatusScreenParams {
        val formalAccountUri = accountUri?.decodeAsUri()?.let { FormalUri.from(it) }
        val formalReplyToBlogAcct = replyBlogAcct?.let { WebFinger.decodeFromUrlString(it) }
        val formalReplyVisibility = replyVisibility?.let(StatusVisibility::valueOf)
        if (formalReplyToBlogAcct != null
            && !replyAuthorName.isNullOrEmpty()
            && !replyBlogId.isNullOrEmpty()
        ) {
            return PostStatusScreenParams.ReplyStatusParams(
                accountUri = formalAccountUri,
                replyToBlogWebFinger = formalReplyToBlogAcct,
                replyToBlogId = replyBlogId,
                replyAuthorName = replyAuthorName,
                replyVisibility = formalReplyVisibility ?: StatusVisibility.PUBLIC,
            )
        }
        if (!editBlog.isNullOrEmpty()) {
            val blog = editBlog.decodeAsUri().let {
                runCatching { Json.decodeFromString<Blog>(it) }.getOrNull()
            }
            if (blog != null) {
                return PostStatusScreenParams.EditStatusParams(formalAccountUri, blog)
            }
        }
        return PostStatusScreenParams.PostStatusParams(formalAccountUri)
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
