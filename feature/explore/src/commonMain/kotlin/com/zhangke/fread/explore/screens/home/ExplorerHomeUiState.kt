package com.zhangke.fread.explore.screens.home

import com.zhangke.framework.composable.PagerTab
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform

data class ExplorerHomeUiState(
    val selectedAccount: LoggedAccount?,
    val accountWithTabList: List<Pair<LoggedAccount, PagerTab>>,
) {

    val locator: PlatformLocator?
        get() {
            if (selectedAccount == null) return null
            return PlatformLocator(
                baseUrl = selectedAccount.platform.baseUrl,
                accountUri = selectedAccount.uri,
            )
        }

    val platform: BlogPlatform? get() = selectedAccount?.platform

    val tab: PagerTab?
        get() {
            return accountWithTabList.firstOrNull { it.first.uri == selectedAccount?.uri }?.second
        }

    companion object {

        fun default(): ExplorerHomeUiState {
            return ExplorerHomeUiState(
                selectedAccount = null,
                accountWithTabList = emptyList(),
            )
        }
    }
}
