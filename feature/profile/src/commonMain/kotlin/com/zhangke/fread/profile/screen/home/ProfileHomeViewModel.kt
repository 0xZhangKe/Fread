package com.zhangke.fread.profile.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.analytics.reportInfo
import com.zhangke.fread.common.content.FreadContentRepo
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class ProfileHomeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val contentRepo: FreadContentRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileHomeUiState(emptyList()))
    val uiState: StateFlow<ProfileHomeUiState> get() = _uiState.asStateFlow()

    private val _openPageFlow = MutableSharedFlow<Screen>()
    val openPageFlow = _openPageFlow.asSharedFlow()

    private var refreshAccountJob: Job? = null

    init {
        observeAccountFlow()
    }

    private fun observeAccountFlow() {
        viewModelScope.launch {
            statusProvider.accountManager
                .getAllAccountFlow()
                .map { list -> list.map { ProfileAccountUiState(it, true) } }
                .map { list -> list.groupBy { it.account.platform }.map { it.key to it.value } }
                .collect { list ->
                    _uiState.update { it.copy(accountDataList = list) }
                    reportInfo { put("accountCount", list.size.toString()) }
                }
        }
    }

    fun refreshAccountInfo() {
        if (refreshAccountJob?.isActive == true) return
        refreshAccountJob = launchInViewModel {
            val refreshedList = statusProvider.accountManager.refreshAllAccountInfo()
            _uiState.update { state ->
                state.copy(
                    accountDataList = state.accountDataList.map { group ->
                        group.first to group.second.map { account ->
                            val result =
                                refreshedList.firstOrNull { it.account.uri == account.account.uri }
                            val logged = when (result) {
                                is AccountRefreshResult.Success -> true
                                is AccountRefreshResult.Failure -> !result.error.isAuthenticationFailure
                                null -> account.logged
                            }
                            account.copy(logged = logged)
                        }
                    }
                )
            }
        }
    }

    fun onLogoutClick(account: LoggedAccount) {
        viewModelScope.launch {
            statusProvider.accountManager.logout(account)
            statusProvider.accountManager
                .selectContentWithAccount(contentRepo.getAllContent(), account)
                .forEach { contentRepo.delete(it.id) }
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

    fun onFavouritedClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getFavouritedScreen(
                    locator = account.platformLocator,
                    protocol = account.platform.protocol,
                )?.let { _openPageFlow.emit(it) }
        }
    }

    fun onBookmarkedClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getBookmarkedScreen(
                    locator = account.platformLocator,
                    protocol = account.platform.protocol,
                )?.let { _openPageFlow.emit(it) }
        }
    }

    fun onFollowedHashtagClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getFollowedHashtagScreen(
                    locator = PlatformLocator(
                        baseUrl = account.platform.baseUrl,
                        accountUri = account.uri,
                    ),
                    protocol = account.platform.protocol,
                )?.let { _openPageFlow.emit(it) }
        }
    }

    fun onPinnedFeedsClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getEditContentConfigScreenScreen(account)
                ?.let { _openPageFlow.emit(it) }
        }
    }

    fun onLoginClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.accountManager.triggerAuthBySource(account.platform)
        }
    }

    fun onListsClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getCreatedListScreen(
                    locator = account.platformLocator,
                    platform = account.platform,
                )?.let { _openPageFlow.emit(it) }
        }
    }

    private val LoggedAccount.platformLocator: PlatformLocator
        get() = PlatformLocator(
            baseUrl = platform.baseUrl,
            accountUri = uri,
        )
}
