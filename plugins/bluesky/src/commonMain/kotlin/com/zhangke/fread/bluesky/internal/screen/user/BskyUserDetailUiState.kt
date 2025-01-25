package com.zhangke.fread.bluesky.internal.screen.user

import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.ui.common.RelationshipUiState

data class BskyUserDetailUiState(
    val loading: Boolean,
    val loadError: Throwable?,
    val did: String,
    val handle: String?,
    val displayName: String?,
    val description: String?,
    val avatar: String?,
    val banner: String?,
    val followersCount: Long?,
    val followsCount: Long?,
    val postsCount: Long?,
    val relationship: RelationshipUiState,
    val tabs: List<BlueskyFeeds>,
) {

    companion object {

        fun default(did: String): BskyUserDetailUiState {
            return BskyUserDetailUiState(
                loading = false,
                loadError = null,
                did = did,
                handle = null,
                displayName = null,
                description = null,
                avatar = null,
                banner = null,
                followersCount = null,
                followsCount = null,
                postsCount = null,
                relationship = RelationshipUiState.UNKNOWN,
                tabs = emptyList(),
            )
        }
    }
}
