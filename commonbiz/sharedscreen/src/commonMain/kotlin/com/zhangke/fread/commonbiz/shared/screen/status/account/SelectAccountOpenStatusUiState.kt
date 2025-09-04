package com.zhangke.fread.commonbiz.shared.screen.status.account

import com.zhangke.fread.status.account.LoggedAccount

data class SelectAccountOpenStatusUiState(
    val loadingAccounts: Boolean,
    val accountList: List<LoggedAccount>,
    val searching: Boolean,
    val searchingAccount: LoggedAccount?,
    val searchFailed: Boolean,
){

    companion object{

        fun default(loadingAccounts: Boolean): SelectAccountOpenStatusUiState{
            return SelectAccountOpenStatusUiState(
                loadingAccounts = loadingAccounts,
                accountList = emptyList(),
                searching = false,
                searchingAccount = null,
                searchFailed = false,
            )
        }
    }
}
