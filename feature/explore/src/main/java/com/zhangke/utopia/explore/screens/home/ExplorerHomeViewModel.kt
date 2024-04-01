package com.zhangke.utopia.explore.screens.home

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
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ExplorerHomeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    companion object {

        private const val LATEST_SELECTED_ACCOUNT = "last_selected_account"
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
                        selectedAccount = accountsList.firstOrNull { it.uri == latestSelectedAccount }
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
