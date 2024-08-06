package com.zhangke.fread.activitypub.app.internal.screen.user.common

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole

data class CommonUserUiState(
    val role: IdentityRole,
    val loading: Boolean,
    val userList: List<BlogAuthor>,
    val loadMoreState: LoadState,
) {

    companion object {

        fun default(role: IdentityRole) = CommonUserUiState(
            role = role,
            loading = false,
            userList = emptyList(),
            loadMoreState = LoadState.Idle,
        )
    }
}
