package com.zhangke.fread.explore.screens.home

import com.zhangke.framework.composable.PagerTab
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform

data class ExplorerHomeUiState(
    val selectedAccount: LoggedAccount?,
    val loggedAccountsList: List<LoggedAccount>,
    val tab: PagerTab?,
) {

    val role: IdentityRole?
        get() {
            if (selectedAccount == null) return null
            return IdentityRole(
                accountUri = selectedAccount.uri,
                baseUrl = selectedAccount.platform.baseUrl,
            )
        }

    val platform: BlogPlatform? get() = selectedAccount?.platform

    companion object {

        fun default(): ExplorerHomeUiState {
            return ExplorerHomeUiState(
                selectedAccount = null,
                loggedAccountsList = emptyList(),
                tab = null,
            )
        }
    }
}
