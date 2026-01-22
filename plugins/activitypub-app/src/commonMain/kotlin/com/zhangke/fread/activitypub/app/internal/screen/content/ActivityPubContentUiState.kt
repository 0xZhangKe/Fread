package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.model.PlatformLocator

data class ActivityPubContentUiState(
    val locator: PlatformLocator?,
    val config: ActivityPubContent?,
    val account: ActivityPubLoggedAccount?,
    val showAccountInTopBar: Boolean,
    val errorMessage: String?,
    val showRefreshButton: Boolean,
    val showNextButton: Boolean,
) {

    companion object {

        fun default(): ActivityPubContentUiState {
            return ActivityPubContentUiState(
                locator = null,
                config = null,
                account = null,
                showAccountInTopBar = false,
                errorMessage = null,
                showRefreshButton = false,
                showNextButton = false,
            )
        }
    }
}
