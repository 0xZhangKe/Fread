package com.zhangke.fread.profile.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.collections.container
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.analytics.reportInfo
import com.zhangke.fread.common.routeScreen
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.krouter.KRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileHomeViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
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
        var latestAccountList: List<LoggedAccount>? = null
        viewModelScope.launch {
            statusProvider.accountManager
                .getAllAccountFlow()
                .map { list -> list.groupBy(LoggedAccount::platform).map { it.key to it.value } }
                .collect { list ->
                    _uiState.update {
                        it.copy(accountDataList = list)
                    }
                    val newAccountList = list.flatMap { it.second }
                    if (latestAccountList != null && latestAccountList!!.size < newAccountList.size) {
                        // has new added account
                        val newAccount = newAccountList.firstOrNull { account ->
                            !latestAccountList!!.container { it.uri == account.uri }
                        }
                        newAccount?.let {
                            onNewAccountAdded(it)
                        }
                    }
                    latestAccountList = newAccountList
                    reportInfo {
                        put("accountCount", newAccountList.size.toString())
                    }
                }
        }
    }

    fun refreshAccountInfo() {
        if (refreshAccountJob?.isActive == true) return
        refreshAccountJob = launchInViewModel {
            statusProvider.accountManager.refreshAllAccountInfo()
        }
    }

    private suspend fun onNewAccountAdded(account: LoggedAccount) {
        val hasContentOfThisAccountPlatform = contentConfigRepo.getAllConfig()
            .filterIsInstance<ContentConfig.ActivityPubContent>()
            .firstOrNull { it.baseUrl == account.platform.baseUrl } != null
        if (hasContentOfThisAccountPlatform) return
        contentConfigRepo.insertActivityPubContent(account.platform)
    }

    fun onLogoutClick(account: LoggedAccount) {
        viewModelScope.launch {
            statusProvider.accountManager.logout(account.uri)
        }
    }

    fun onAccountClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getUserDetailRoute(IdentityRole(account.uri, null), account.uri)
                ?.let { KRouter.routeScreen(it) }
                ?.let { _openPageFlow.emit(it) }
        }
    }

    fun onFavouritedClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getFavouritedScreen(
                    role = IdentityRole(account.uri, null),
                    protocol = account.platform.protocol,
                )
                ?.let { KRouter.routeScreen(it) }
                ?.let { _openPageFlow.emit(it) }
        }
    }

    fun onBookmarkedClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getBookmarkedScreen(
                    role = IdentityRole(account.uri, null),
                    protocol = account.platform.protocol,
                )
                ?.let { KRouter.routeScreen(it) }
                ?.let { _openPageFlow.emit(it) }
        }
    }

    fun onFollowedHashtagClick(account: LoggedAccount) {
        launchInViewModel {
            statusProvider.screenProvider
                .getFollowedHashtagScreen(
                    role = IdentityRole(account.uri, null),
                    protocol = account.platform.protocol,
                )
                ?.let { KRouter.routeScreen(it) }
                ?.let { _openPageFlow.emit(it) }
        }
    }
}
