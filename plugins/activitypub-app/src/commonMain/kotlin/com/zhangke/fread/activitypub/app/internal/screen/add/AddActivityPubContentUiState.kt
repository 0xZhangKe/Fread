package com.zhangke.fread.activitypub.app.internal.screen.add

import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount

data class AddActivityPubContentUiState(
    val contentExist: Boolean,
    val account: ActivityPubLoggedAccount?,
)
