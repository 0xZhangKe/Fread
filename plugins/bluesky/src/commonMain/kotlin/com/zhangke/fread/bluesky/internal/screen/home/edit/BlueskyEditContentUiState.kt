package com.zhangke.fread.bluesky.internal.screen.home.edit

import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.status.model.IdentityRole

data class BlueskyEditContentUiState(
    val content: BlueskyContent,
) {

    val role: IdentityRole
        get() {
            return IdentityRole(
                accountUri = null,
                baseUrl = content.baseUrl,
            )
        }
}
