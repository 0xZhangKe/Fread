package com.zhangke.fread.bluesky.internal.model

import androidx.compose.runtime.Composable
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_feeds_following_name
import com.zhangke.fread.bluesky.bsky_feeds_user_likes
import com.zhangke.fread.bluesky.bsky_feeds_user_medias
import com.zhangke.fread.bluesky.bsky_feeds_user_posts
import com.zhangke.fread.bluesky.bsky_feeds_user_replies
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/**
 * Feeds 一词包含两个含义，一是广义上的 Feeds 信息流。
 * 二是 Bluesky 中的 Feeds，Bluesky 中的 Feeds 是由别人创建的，由 FeedsGenerator 生成的 Feeds。
 * BlueskyFeeds 类中的 Feeds 是广义上的 Feeds，包含着 FeedsGenerator 生成的 Feeds，
 * 也包含 Following Timeline 和 用户自己创建的 List。
 */
@Serializable
sealed class BlueskyFeeds {

    /**
     * pinned to Home Screen
     */
    abstract val pinned: Boolean

    @Composable
    abstract fun displayName(): String

    @Serializable
    data class Following(
        override val pinned: Boolean,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName(): String {
            return stringResource(Res.string.bsky_feeds_following_name)
        }
    }

    @Serializable
    data class Feeds(
        val uri: String,
        val cid: String,
        val did: String,
        override val pinned: Boolean,
        val displayName: String,
        val description: String? = null,
        val avatar: String? = null,
        val likeCount: Long? = null,
        val likedRecord: String? = null,
        val creator: BlueskyProfile,
    ) : BlueskyFeeds() {

        val liked: Boolean get() = !likedRecord.isNullOrEmpty()

        @Composable
        override fun displayName(): String {
            return displayName
        }
    }

    @Serializable
    data class List(
        val id: String,
        val uri: String,
        val name: String,
        val description: String? = null,
        val avatar: String? = null,
        override val pinned: Boolean,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName(): String {
            return name
        }
    }

    @Serializable
    data class Hashtags(
        val hashtag: String,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName() = hashtag
    }

    @Serializable
    data class UserPosts(
        val did: String,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName(): String {
            return stringResource(Res.string.bsky_feeds_user_posts)
        }
    }

    @Serializable
    data class UserReplies(
        val did: String,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName(): String {
            return stringResource(Res.string.bsky_feeds_user_replies)
        }
    }

    @Serializable
    data class UserMedias(
        val did: String?,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName(): String {
            return stringResource(Res.string.bsky_feeds_user_medias)
        }
    }

    @Serializable
    data class UserLikes(
        val did: String? = null,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        @Composable
        override fun displayName(): String {
            return stringResource(Res.string.bsky_feeds_user_likes)
        }
    }
}
