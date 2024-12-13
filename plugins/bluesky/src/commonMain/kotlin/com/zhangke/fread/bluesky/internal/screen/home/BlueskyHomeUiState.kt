package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole

data class BlueskyHomeUiState(
    val role: IdentityRole,
    val config: ContentConfig.BlueskyContent?,
    val account: BlueskyLoggedAccount?,
    val errorMessage: String? = null,
) {

    companion object {

        fun default(
            role: IdentityRole = IdentityRole.nonIdentityRole,
            config: ContentConfig.BlueskyContent? = null,
            account: BlueskyLoggedAccount? = null,
            errorMessage: String? = null
        ): BlueskyHomeUiState {
            return BlueskyHomeUiState(
                role = role,
                config = config,
                account = account,
                errorMessage = errorMessage,
            )
        }
    }
}
