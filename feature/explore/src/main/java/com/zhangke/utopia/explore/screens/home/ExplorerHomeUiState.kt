package com.zhangke.utopia.explore.screens.home

import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.model.IdentityRole

data class ExplorerHomeUiState(
    val selectedAccount: LoggedAccount?,
    val role: IdentityRole?,
    val loggedAccountsList: List<LoggedAccount>,
)
