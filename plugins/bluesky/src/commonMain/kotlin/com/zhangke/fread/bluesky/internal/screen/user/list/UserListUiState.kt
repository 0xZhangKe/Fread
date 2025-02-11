package com.zhangke.fread.bluesky.internal.screen.user.list

import com.zhangke.fread.status.author.BlogAuthor

data class UserListUiState(
    val userList: List<BlogAuthor>,
) {

    companion object {

        fun default(): UserListUiState {
            return UserListUiState(
                userList = emptyList(),
            )
        }
    }
}
