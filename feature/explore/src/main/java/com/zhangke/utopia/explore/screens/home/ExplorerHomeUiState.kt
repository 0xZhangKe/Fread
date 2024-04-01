package com.zhangke.utopia.explore.screens.home

import com.zhangke.utopia.status.account.LoggedAccount

data class ExplorerHomeUiState(
    val selectedAccount: LoggedAccount?,
    val loggedAccountsList: List<LoggedAccount>,
)
