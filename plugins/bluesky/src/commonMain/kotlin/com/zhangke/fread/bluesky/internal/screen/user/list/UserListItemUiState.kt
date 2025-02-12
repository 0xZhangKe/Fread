package com.zhangke.fread.bluesky.internal.screen.user.list

import com.zhangke.fread.status.author.BlogAuthor

data class UserListItemUiState(
    val author: BlogAuthor,
    val did: String,
    val followedBy: Boolean,
    val followingUri: String?,
    val blockUri: String?,
    val muted: Boolean,
) {

    val following: Boolean get() = !followingUri.isNullOrEmpty()

    val blocked: Boolean get() = !blockUri.isNullOrEmpty()
}
