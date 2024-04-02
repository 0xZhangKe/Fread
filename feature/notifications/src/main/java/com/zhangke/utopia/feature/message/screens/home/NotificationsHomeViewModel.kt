package com.zhangke.utopia.feature.message.screens.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.config.LocalConfigManager
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.uri.FormalUri
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class NotificationsHomeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @ApplicationContext private val context: Context,
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
        return LocalConfigManager.getString(context, LATEST_SELECTED_ACCOUNT)?.let { FormalUri.from(it) }
    }

    private suspend fun updateLatestSelectedAccount(accountUri: FormalUri) {
        LocalConfigManager.putString(context, LATEST_SELECTED_ACCOUNT, accountUri.toString())
    }
}
