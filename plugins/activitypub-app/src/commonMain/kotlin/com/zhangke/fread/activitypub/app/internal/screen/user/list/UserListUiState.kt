package com.zhangke.fread.activitypub.app.internal.screen.user.list

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole

data class UserListUiState(
    val type: UserListType,
    val role: IdentityRole,
    val loading: Boolean,
    val userList: List<BlogAuthorUiState>,
    val loadMoreState: LoadState,
)

data class BlogAuthorUiState(
    val author: BlogAuthor,
    val following: Boolean? = null,
)
