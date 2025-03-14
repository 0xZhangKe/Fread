package com.zhangke.fread.bluesky.internal.screen.feeds.detail

data class FeedsDetailUiState(
    val initializing: Boolean,
    val avatar: String?,
    val name: String?,
    val authorHandle: String?,
    val description: String?,
    val likedCount: Long?,
    val liked: Boolean,
    val pinned: Boolean,
) {

    companion object {

        fun default(): FeedsDetailUiState {
            return FeedsDetailUiState(
                initializing = false,
                avatar = null,
                name = null,
                authorHandle = null,
                description = null,
                likedCount = null,
                liked = false,
                pinned = false,
            )
        }
    }
}
