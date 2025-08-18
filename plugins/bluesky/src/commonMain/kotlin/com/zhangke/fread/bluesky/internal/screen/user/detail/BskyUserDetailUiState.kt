package com.zhangke.fread.bluesky.internal.screen.user.detail

import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.Relationships

data class BskyUserDetailUiState(
    val loading: Boolean,
    val loadError: Throwable?,
    val did: String,
    val handle: String?,
    val userHomePageUrl: String?,
    val displayName: String?,
    val description: String?,
    val avatar: String?,
    val banner: String?,
    val isOwner: Boolean,
    val followersCount: Long?,
    val followsCount: Long?,
    val postsCount: Long?,
    val relationship: Relationships?,
    val followUri: String?,
    val blockUri: String?,
    val muted: Boolean,
    val tabs: List<BlueskyFeeds>,
) {

    val blocked: Boolean get() = !blockUri.isNullOrEmpty()

    companion object {

        fun default(did: String): BskyUserDetailUiState {
            return BskyUserDetailUiState(
                loading = false,
                loadError = null,
                did = did,
                handle = null,
                isOwner = false,
                displayName = null,
                description = null,
                avatar = null,
                banner = null,
                userHomePageUrl = null,
                followersCount = null,
                followsCount = null,
                postsCount = null,
                relationship = null,
                followUri = null,
                blockUri = null,
                muted = false,
                tabs = emptyList(),
            )
        }
    }
}
