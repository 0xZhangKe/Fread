package com.zhangke.fread.feature.message.screens.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class NotificationsHomeViewModel(
    private val statusProvider: StatusProvider,
    private val activeAccountsSynchronizer: ActiveAccountsSynchronizer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotificationsHomeUiState(
            selectedAccount = null,
            accountList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { accounts ->
                    var selectedAccount = _uiState.value.selectedAccount
                    if (selectedAccount == null) {
                        val lastActiveAccountUri =
                            activeAccountsSynchronizer.activeAccountUriFlow.value
                        if (!lastActiveAccountUri.isNullOrEmpty()) {
                            selectedAccount =
                                accounts.firstOrNull { it.uri.toString() == lastActiveAccountUri }
                        }
                    }
                    if (selectedAccount == null) {
                        selectedAccount = accounts.firstOrNull()
                    }
                    _uiState.update {
                        it.copy(
                            accountList = accounts,
                            selectedAccount = selectedAccount,
                        )
                    }
                }
        }
        launchInViewModel {
            activeAccountsSynchronizer.activeAccountUriFlow
                .mapNotNull { it?.takeIf { it.isNotEmpty() } }
                .collect { lastActiveAccountUri ->
                    val accounts = uiState.value.accountList
                    val selectedAccount =
                        accounts.firstOrNull { it.uri.toString() == lastActiveAccountUri }
                    if (selectedAccount != null && selectedAccount.uri != uiState.value.selectedAccount?.uri) {
                        _uiState.update { it.copy(selectedAccount = selectedAccount) }
                    }
                }
        }
    }

    fun onAccountSelected(account: LoggedAccount) {
        if (account.uri == uiState.value.selectedAccount?.uri) return
        launchInViewModel {
            _uiState.update { it.copy(selectedAccount = account) }
            activeAccountsSynchronizer.onAccountSelected(account.uri.toString())
        }
    }
}
