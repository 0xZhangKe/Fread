package com.zhangke.fread.explore.screens.home

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.IdentityRole

data class ExplorerHomeUiState(
    val selectedAccount: LoggedAccount?,
    val loggedAccountsList: List<LoggedAccount>,
) {

    val role: IdentityRole get() = IdentityRole(accountUri = selectedAccount?.uri, baseUrl = null)
}
