package com.zhangke.fread.activitypub.app.internal.screen.user.common

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.author.BlogAuthor

data class CommonUserUiState(
    val loading: Boolean,
    val userList: List<BlogAuthor>,
    val loadMoreState: LoadState,
) {

    companion object {

        fun default() = CommonUserUiState(
            loading = false,
            userList = emptyList(),
            loadMoreState = LoadState.Idle,
        )
    }
}
