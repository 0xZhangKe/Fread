package com.zhangke.fread.activitypub.app.internal.screen.status.post

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.json.Json

object PostStatusScreenRoute {

    fun buildReplyScreen(
        accountUri: FormalUri,
        blog: Blog,
    ): PostStatusScreen {
        return PostStatusScreen(
            accountUri = accountUri,
            replyBlogId = blog.id,
            replyBlogAcct = blog.author.webFinger,
            replyAuthorName = blog.author.name,
            replyVisibility = blog.visibility.name,
        )
    }

    fun buildEditBlogRoute(
        accountUri: FormalUri,
        blog: Blog,
    ): Screen {
        return PostStatusScreen(
            accountUri = accountUri,
            editBlogJsonString = Json.encodeToString(Blog.serializer(), blog),
        )
    }

    fun buildParams(
        accountUri: FormalUri,
        editBlog: String?,
        replyBlogAcct: WebFinger?,
        replyBlogId: String?,
        replyAuthorName: String?,
        replyVisibility: String?,
    ): PostStatusScreenParams {
        val formalReplyVisibility = replyVisibility?.let(StatusVisibility::valueOf)
        if (replyBlogAcct != null && !replyBlogId.isNullOrEmpty()) {
            return PostStatusScreenParams.ReplyStatusParams(
                accountUri = accountUri,
                replyToBlogWebFinger = replyBlogAcct,
                replyToBlogId = replyBlogId,
                replyAuthorName = replyAuthorName.orEmpty(),
                replyVisibility = formalReplyVisibility ?: StatusVisibility.PUBLIC,
            )
        }
        if (!editBlog.isNullOrEmpty()) {
            val blog = runCatching { Json.decodeFromString<Blog>(editBlog) }.getOrNull()
            if (blog != null) {
                return PostStatusScreenParams.EditStatusParams(accountUri, blog)
            }
        }
        return PostStatusScreenParams.PostStatusParams(accountUri)
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
