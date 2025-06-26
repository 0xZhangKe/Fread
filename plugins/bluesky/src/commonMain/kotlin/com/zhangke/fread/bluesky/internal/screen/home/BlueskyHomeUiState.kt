package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.status.model.PlatformLocator

data class BlueskyHomeUiState(
    val initializing: Boolean,
    val locator: PlatformLocator?,
    val content: BlueskyContent?,
    val account: BlueskyLoggedAccount?,
    val errorMessage: String? = null,
) {

    companion object {

        fun default(
            initializing: Boolean,
            locator: PlatformLocator? = null,
            config: BlueskyContent? = null,
            account: BlueskyLoggedAccount? = null,
            errorMessage: String? = null
        ): BlueskyHomeUiState {
            return BlueskyHomeUiState(
                initializing = initializing,
                locator = locator,
                content = config,
                account = account,
                errorMessage = errorMessage,
            )
        }
    }
}
