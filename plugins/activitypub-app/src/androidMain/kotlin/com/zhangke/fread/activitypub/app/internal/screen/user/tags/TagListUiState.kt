package com.zhangke.fread.activitypub.app.internal.screen.user.tags

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole

data class TagListUiState(
    val role: IdentityRole,
    val refreshing: Boolean,
    val tags: List<Hashtag>,
    val loadState: LoadState,
) {

    companion object {

        fun default(role: IdentityRole = IdentityRole.nonIdentityRole) = TagListUiState(
            role = role,
            refreshing = false,
            tags = emptyList(),
            loadState = LoadState.Idle,
        )
    }
}
