package com.zhangke.fread.feature.message.screens.home

import com.zhangke.framework.composable.PagerTab
import com.zhangke.fread.status.account.LoggedAccount

data class NotificationsHomeUiState(
    val selectedAccount: LoggedAccount? = null,
    val accountList: List<LoggedAccount>,
    val accountToTabList: List<Pair<LoggedAccount, PagerTab>>,
)
