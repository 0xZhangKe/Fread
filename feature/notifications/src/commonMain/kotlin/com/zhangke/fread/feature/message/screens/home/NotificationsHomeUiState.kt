package com.zhangke.fread.feature.message.screens.home

import com.zhangke.framework.nav.Tab
import com.zhangke.fread.feature.message.screens.notification.NotificationTab
import com.zhangke.fread.status.account.LoggedAccount

data class NotificationsHomeUiState(
    val selectedAccount: LoggedAccount? = null,
    val accountList: List<LoggedAccount>,
) {

    val tabs: List<Tab> = accountList.map { NotificationTab(it) }
}
