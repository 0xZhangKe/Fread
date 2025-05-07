package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import com.zhangke.fread.status.account.LoggedAccount

data class MultiAccountPublishingUiState(
    val accounts: List<LoggedAccount>,
    val publishing: Boolean,
)
