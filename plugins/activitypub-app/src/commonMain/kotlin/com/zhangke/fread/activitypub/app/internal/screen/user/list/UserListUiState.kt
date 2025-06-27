package com.zhangke.fread.activitypub.app.internal.screen.user.list

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator

data class UserListUiState(
    val type: UserListType,
    val locator: PlatformLocator,
    val loading: Boolean,
    val userList: List<BlogAuthorUiState>,
    val loadMoreState: LoadState,
)

data class BlogAuthorUiState(
    val author: BlogAuthor,
    val following: Boolean? = null,
)
