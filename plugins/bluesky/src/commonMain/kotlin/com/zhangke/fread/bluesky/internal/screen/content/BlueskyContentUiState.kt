package com.zhangke.fread.bluesky.internal.screen.content

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.status.model.PlatformLocator

data class BlueskyContentUiState(
    val initializing: Boolean,
    val locator: PlatformLocator?,
    val content: BlueskyContent?,
    val account: BlueskyLoggedAccount?,
    val authExpired: Boolean?,
    val showAccountInTopBar: Boolean,
    val showRefreshButton: Boolean,
    val showNextButton: Boolean,
    val errorMessage: String? = null,
) {

    val authFailed: Boolean
        get() {
            if (initializing) return false
            if (content?.feedsList.isNullOrEmpty() && account == null) return true
            return authExpired == true
        }

    companion object {

        fun default(
            initializing: Boolean,
            locator: PlatformLocator? = null,
            config: BlueskyContent? = null,
            account: BlueskyLoggedAccount? = null,
            errorMessage: String? = null
        ): BlueskyContentUiState {
            return BlueskyContentUiState(
                initializing = initializing,
                locator = locator,
                content = config,
                account = account,
                authExpired = null,
                showAccountInTopBar = false,
                errorMessage = errorMessage,
                showRefreshButton = false,
                showNextButton = false,
            )
        }
    }
}
