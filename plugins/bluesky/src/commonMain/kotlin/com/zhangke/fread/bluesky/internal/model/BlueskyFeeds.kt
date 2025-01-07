package com.zhangke.fread.bluesky.internal.model

import androidx.compose.runtime.Composable
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_feeds_following_name
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/**
 * Feeds 一词包含两个含义，一是广义上的 Feeds 信息流。
 * 二是 Bluesky 中的 Feeds，Bluesky 中的 Feeds 是由别人创建的，由 FeedsGenerator 生成的 Feeds。
 * BlueskyFeeds 类中的 Feeds 是广义上的 Feeds，包含着 FeedsGenerator 生成的 Feeds，
 * 也包含 Following Timeline 和 用户自己创建的 List。
 */
@Serializable
sealed interface BlueskyFeeds {

    /**
     * pinned to Home Screen
     */
    val pinned: Boolean

    val following: Boolean

    @Composable
    fun displayName(): String

    @Serializable
    data class Following(
        override val following: Boolean,
        override val pinned: Boolean,
    ) : BlueskyFeeds {

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
        override val following: Boolean,
        override val pinned: Boolean,
        val displayName: String,
        val description: String?,
        val avatar: String?,
        val likeCount: Long?,
        val creator: BlueskyProfile,
    ) : BlueskyFeeds {

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
        val description: String?,
        val avatar: String?,
        override val following: Boolean,
        override val pinned: Boolean,
    ) : BlueskyFeeds {

        @Composable
        override fun displayName(): String {
            return name
        }
    }
}
