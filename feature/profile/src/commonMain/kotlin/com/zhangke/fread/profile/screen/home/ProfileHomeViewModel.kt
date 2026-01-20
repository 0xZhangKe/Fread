package com.zhangke.fread.profile.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.collections.container
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.account.isAuthenticationFailure
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileHomeViewModel(
    private val statusProvider: StatusProvider,
    private val activeAccountsSynchronizer: ActiveAccountsSynchronizer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileHomeUiState(emptyList()))
    val uiState: StateFlow<ProfileHomeUiState> get() = _uiState.asStateFlow()

    private val _openPageFlow = MutableSharedFlow<NavKey>()
    val openPageFlow = _openPageFlow.asSharedFlow()

    private var refreshAccountJob: Job? = null

    init {
        observeAccountFlow()
        launchInViewModel {
            activeAccountsSynchronizer.activeAccountUriFlow
                .mapNotNull { it?.takeIf { it.isNotEmpty() } }
                .collect { lastActiveAccountUri ->
                    _uiState.update { state ->
                        state.copy(
                            accountDataList = state.accountDataList.map { account ->
                                account.copy(
                                    active = account.account.account.uri.toString() == lastActiveAccountUri,
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun observeAccountFlow() {
        viewModelScope.launch {
            statusProvider.accountManager
                .getAllAccountDetailFlow()
                .map { list ->
                    val activeAccountUri = activeAccountsSynchronizer.activeAccountUriFlow.value
                    val dirtyAccountList = uiState.value.accountDataList
                    list.map { account ->
                        val authFailed = dirtyAccountList.firstOrNull {
                            it.account.account.uri == account.account.uri
                        }?.authFailed ?: false
                        ProfileAccountUiState(
                            account = account,
                            authFailed = authFailed,
                            active = account.account.uri.toString() == activeAccountUri,
                        )
                    }
                }
                .collect { list ->
                    _uiState.update { it.copy(accountDataList = list) }
                }
        }
    }

    fun refreshAccountInfo() {
        if (refreshAccountJob?.isActive == true) return
        refreshAccountJob = launchInViewModel {
            val refreshedList = statusProvider.accountManager.refreshAllAccountInfo()
            val authFailedAccounts = refreshedList.filter {
                it is AccountRefreshResult.Failure && it.error.isAuthenticationFailure
            }
            _uiState.update { state ->
                state.copy(
                    accountDataList = state.accountDataList
                        .map { account ->
                            val authFailed = authFailedAccounts.container {
                                it.account.uri == account.account.account.uri
                            }
                            account.copy(authFailed = authFailed)
                        }
                )
            }
        }
    }

    fun onAccountClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getUserDetailScreen(
                    locator = account.platformLocator,
                    uri = account.uri,
                    userId = account.id,
                )?.let { _openPageFlow.emit(it) }
        }
    }

    fun onLoginClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.accountManager.triggerAuthBySource(account.platform, account)
        }
    }

    private val LoggedAccount.platformLocator: PlatformLocator
        get() = PlatformLocator(
            baseUrl = platform.baseUrl,
            accountUri = uri,
        )
}
