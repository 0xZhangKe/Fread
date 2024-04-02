package com.zhangke.utopia.feature.message.screens.home

import com.zhangke.framework.composable.PagerTab
import com.zhangke.utopia.status.account.LoggedAccount

data class NotificationsHomeUiState(
    val selectedAccount: LoggedAccount? = null,
    val accountList: List<LoggedAccount>,
    val accountToTabList: List<Pair<LoggedAccount, PagerTab>>,
)
