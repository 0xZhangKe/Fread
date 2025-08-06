package com.zhangke.fread.explore.screens.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class ExplorerHomeViewModel @Inject constructor(
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
                        loggedAccountsList = accountsList,
                        selectedAccount = selectedAccount,
                    )
                    _uiState.value = newUiState.copy(tab = getPagerTab(newUiState))
                }
        }
        launchInViewModel {
            activeAccountsSynchronizer.activeAccountUriFlow
                .mapNotNull { it?.takeIf { it.isNotEmpty() } }
                .collect { lastActiveAccountUri ->
                    val accounts = uiState.value.loggedAccountsList
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
            val newUiState = _uiState.value.copy(selectedAccount = account)
            _uiState.update { newUiState.copy(tab = getPagerTab(newUiState)) }
            activeAccountsSynchronizer.onAccountSelected(account.uri.toString())
        }
    }

    private fun getPagerTab(uiState: ExplorerHomeUiState): PagerTab? {
        return if (uiState.locator != null && uiState.platform != null) {
            statusProvider.screenProvider.getExplorerTab(
                locator = uiState.locator!!,
                platform = uiState.platform!!,
            )
        } else {
            null
        }
    }
}
