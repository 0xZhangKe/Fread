package com.zhangke.fread.activitypub.app.internal.screen.add

import com.zhangke.fread.status.account.LoggedAccount

data class AddActivityPubContentUiState(
    val contentExist: Boolean,
    val account: LoggedAccount?,
)
