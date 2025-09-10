package com.zhangke.fread.bluesky.internal.model

import androidx.compose.runtime.Composable
import com.zhangke.fread.localization.LocalizedString
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

    abstract val id: String

    @Composable
    abstract fun displayName(): String

    @Serializable
    data class FollowingTimeline(
        override val pinned: Boolean,
    ) : BlueskyFeeds() {

        override val id: String get() = "FollowingTimeline"

        @Composable
        override fun displayName(): String {
            return stringResource(LocalizedString.bsky_feeds_following_name)
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

        override val id: String get() = cid

        @Composable
        override fun displayName(): String {
            return displayName
        }
    }

    @Serializable
    data class List(
        override val id: String,
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

        override val id: String get() = hashtag

        @Composable
        override fun displayName() = hashtag
    }

    @Serializable
    data class UserPosts(
        val did: String,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        override val id: String get() = "$did/posts"

        @Composable
        override fun displayName(): String {
            return stringResource(LocalizedString.bsky_feeds_user_posts)
        }
    }

    @Serializable
    data class UserReplies(
        val did: String,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        override val id: String get() = "$did/replies"

        @Composable
        override fun displayName(): String {
            return stringResource(LocalizedString.bsky_feeds_user_replies)
        }
    }

    @Serializable
    data class UserMedias(
        val did: String?,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        override val id: String get() = "$did/medias"

        @Composable
        override fun displayName(): String {
            return stringResource(LocalizedString.bsky_feeds_user_medias)
        }
    }

    @Serializable
    data class UserLikes(
        val did: String? = null,
        override val pinned: Boolean = false,
    ) : BlueskyFeeds() {

        override val id: String get() = "$did/likes"

        @Composable
        override fun displayName(): String {
            return stringResource(LocalizedString.bsky_feeds_user_likes)
        }
    }
}
