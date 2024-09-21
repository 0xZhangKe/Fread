package com.zhangke.fread.feature.message.screens.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class NotificationsHomeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val localConfigManager: LocalConfigManager,
) : ViewModel() {

    companion object {

        private const val LATEST_SELECTED_ACCOUNT = "notification_tab_last_selected_account"
    }

    private val _uiState = MutableStateFlow(
        NotificationsHomeUiState(
            selectedAccount = null,
            accountList = emptyList(),
            accountToTabList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { accounts ->
                    val accountToTabList = accounts.mapNotNull { account ->
                        statusProvider.screenProvider
                            .getNotificationScreen(account)
                            ?.let { account to it }
                    }
                    var selectedAccount = _uiState.value.selectedAccount
                    if (selectedAccount == null) {
                        val latestSelectedAccount = getLastedSelectedAccount()
                        selectedAccount = accounts.firstOrNull { it.uri == latestSelectedAccount }
                    }
                    if (selectedAccount == null) {
                        selectedAccount = accounts.firstOrNull()
                    }
                    _uiState.update {
                        it.copy(
                            accountList = accounts,
                            selectedAccount = selectedAccount,
                            accountToTabList = accountToTabList,
                        )
                    }
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
        return localConfigManager.getString(LATEST_SELECTED_ACCOUNT)?.let { FormalUri.from(it) }
    }

    private suspend fun updateLatestSelectedAccount(accountUri: FormalUri) {
        localConfigManager.putString(LATEST_SELECTED_ACCOUNT, accountUri.toString())
    }
}
