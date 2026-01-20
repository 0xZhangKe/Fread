package com.zhangke.fread.explore.screens.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.nav.Tab
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class ExplorerHomeViewModel(
    private val statusProvider: StatusProvider,
    private val activeAccountsSynchronizer: ActiveAccountsSynchronizer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorerHomeUiState.default())
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { accountsList ->
                    val currentUiState = _uiState.value
                    var selectedAccount = currentUiState.selectedAccount
                    if (selectedAccount == null) {
                        val latestSelectedAccount =
                            activeAccountsSynchronizer.activeAccountUriFlow.value
                        selectedAccount =
                            accountsList.firstOrNull { it.uri.toString() == latestSelectedAccount }
                    }
                    if (selectedAccount == null) {
                        selectedAccount = accountsList.firstOrNull()
                    }
                    val newUiState = currentUiState.copy(
                        accountWithTabList = convertContentsToWithTab(accountsList),
                        selectedAccount = selectedAccount,
                    )
                    _uiState.value = newUiState
                }
        }
        launchInViewModel {
            activeAccountsSynchronizer.activeAccountUriFlow
                .mapNotNull { it?.takeIf { it.isNotEmpty() } }
                .collect { lastActiveAccountUri ->
                    val accounts = uiState.value.accountWithTabList.map { it.first }
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

    private fun convertContentsToWithTab(accounts: List<LoggedAccount>): List<Pair<LoggedAccount, Tab>> {
        return accounts.mapNotNull { account ->
            statusProvider.screenProvider.getExplorerTab(
                locator = PlatformLocator(
                    baseUrl = account.platform.baseUrl,
                    accountUri = account.uri,
                ),
                platform = account.platform,
            )?.let { account to it }
        }
    }
}
