package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.status.model.IdentityRole

data class BlueskyHomeUiState(
    val initializing: Boolean,
    val role: IdentityRole,
    val content: BlueskyContent?,
    val account: BlueskyLoggedAccount?,
    val errorMessage: String? = null,
) {

    companion object {

        fun default(
            initializing: Boolean,
            role: IdentityRole = IdentityRole.nonIdentityRole,
            config: BlueskyContent? = null,
            account: BlueskyLoggedAccount? = null,
            errorMessage: String? = null
        ): BlueskyHomeUiState {
            return BlueskyHomeUiState(
                initializing = initializing,
                role = role,
                content = config,
                account = account,
                errorMessage = errorMessage,
            )
        }
    }
}
