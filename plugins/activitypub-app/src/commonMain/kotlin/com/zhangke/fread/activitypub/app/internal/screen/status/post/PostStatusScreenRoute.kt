package com.zhangke.fread.activitypub.app.internal.screen.status.post

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object PostStatusScreenRoute {

    fun buildReplyScreen(
        accountUri: FormalUri,
        blog: Blog,
    ): PostStatusScreen {
        return PostStatusScreen(
            accountUri = accountUri,
            replyingBlogJsonString = globalJson.encodeToString(serializer(), blog)
        )
    }

    fun buildEditBlogRoute(
        accountUri: FormalUri,
        blog: Blog,
    ): Screen {
        return PostStatusScreen(
            accountUri = accountUri,
            editBlogJsonString = globalJson.encodeToString(serializer(), blog),
        )
    }

    fun buildParams(
        accountUri: FormalUri,
        editBlog: String?,
        replyToBlogJsonString: String?,
    ): PostStatusScreenParams {
        val replyToBlog = replyToBlogJsonString?.let {
            runCatching { globalJson.fromJson<Blog>(it) }.getOrNull()
        }
        if (replyToBlog != null) {
            return PostStatusScreenParams.ReplyStatusParams(
                accountUri = accountUri,
                replyingToBlog = replyToBlog,
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
        val replyingToBlog: Blog,
    ) : PostStatusScreenParams

    data class EditStatusParams(
        override val accountUri: FormalUri?,
        val blog: Blog,
    ) : PostStatusScreenParams
}
