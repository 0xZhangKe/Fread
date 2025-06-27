package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.model.PlatformLocator

data class ActivityPubContentUiState(
    val locator: PlatformLocator?,
    val config: ActivityPubContent?,
    val account: ActivityPubLoggedAccount?,
    val errorMessage: String? = null,
) {

    companion object {

        val DEFAULT = ActivityPubContentUiState(null, null, null)
    }
}
