package com.zhangke.fread.explore.screens.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

class ExplorerHomeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val localConfigManager: LocalConfigManager,
) : ViewModel() {

    companion object {

        private const val LATEST_SELECTED_ACCOUNT = "explorer_tab_last_selected_account"
    }

    private val _uiState = MutableStateFlow(ExplorerHomeUiState(null, emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { accountsList ->
                    val currentUiState = _uiState.value
                    var selectedAccount = currentUiState.selectedAccount
                    if (selectedAccount == null) {
                        val latestSelectedAccount = getLastedSelectedAccount()
                        selectedAccount =
                            accountsList.firstOrNull { it.uri == latestSelectedAccount }
                    }
                    if (selectedAccount == null) {
                        selectedAccount = accountsList.firstOrNull()
                    }
                    _uiState.value = currentUiState.copy(
                        loggedAccountsList = accountsList,
                        selectedAccount = selectedAccount,
                    )
                }
        }
    }

    fun onAccountSelected(account: LoggedAccount) {
        if (account.uri == uiState.value.selectedAccount?.uri) return
        launchInViewModel {
            updateLatestSelectedAccount(account.uri)
            _uiState.value = _uiState.value.copy(selectedAccount = account)
        }
    }

    private suspend fun getLastedSelectedAccount(): FormalUri? {
        return localConfigManager.getString(LATEST_SELECTED_ACCOUNT)
            ?.let { FormalUri.from(it) }
    }

    private suspend fun updateLatestSelectedAccount(accountUri: FormalUri) {
        localConfigManager.putString(LATEST_SELECTED_ACCOUNT, accountUri.toString())
    }
}
