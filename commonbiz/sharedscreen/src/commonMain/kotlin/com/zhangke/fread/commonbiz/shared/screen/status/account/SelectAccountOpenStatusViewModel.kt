package com.zhangke.fread.commonbiz.shared.screen.status.account

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.StatusUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SelectAccountOpenStatusViewModel(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SelectAccountOpenStatusUiState.default(loadingAccounts = false),
    )
    val uiState = _uiState

    private val _searchedStatusFlow = MutableSharedFlow<StatusUiState>()
    val searchedStatusFlow = _searchedStatusFlow

    private var searchJob: Job? = null

    private var blogUrl: String? = null

    fun initialize(statusUiState: StatusUiState) {
        val protocol = statusUiState.status.platform.protocol
        val locator = statusUiState.locator
        this.blogUrl = statusUiState.status.intrinsicBlog.url
        _uiState.update { it.copy(loadingAccounts = true) }
        launchInViewModel {
            val availableAccounts = statusProvider.accountManager
                .getAllLoggedAccount()
                .filter { it.platform.protocol == protocol }
                .filter { it.uri != locator.accountUri }
            _uiState.update {
                it.copy(
                    loadingAccounts = false,
                    accountList = availableAccounts,
                )
            }
        }
    }

    fun onAccountClick(account: LoggedAccount) {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                searching = true,
                searchingAccount = account,
                searchFailed = false,
            )
        }
        searchJob = launchInViewModel {
            statusProvider.searchEngine
                .searchStatusByUrl(
                    protocol = account.platform.protocol,
                    locator = account.locator,
                    url = blogUrl.orEmpty(),
                ).onSuccess { status ->
                    if (status != null) {
                        _searchedStatusFlow.emit(status)
                    } else {
                        _uiState.update { it.copy(searchFailed = true) }
                    }
                }.onFailure {
                    _uiState.update { it.copy(searchFailed = true) }
                }
        }
    }

    fun onSearchFailedClick() {
        _uiState.update {
            it.copy(
                searchFailed = false,
                searching = false,
                searchingAccount = null,
            )
        }
    }

    fun onCancelSearchClick() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                searching = false,
                searchingAccount = null,
            )
        }
    }

    fun clearState() {
        _uiState.update { it.reset() }
        searchJob?.cancel()
    }
}
