package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole

data class ActivityPubContentUiState(
    val role: IdentityRole?,
    val config: ContentConfig.ActivityPubContent?,
    val account: ActivityPubLoggedAccount?,
    val errorMessage: String? = null,
) {

    companion object {

        val DEFAULT = ActivityPubContentUiState(null, null, null)
    }
}
