package com.zhangke.utopia.profile.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileHomeUiState(emptyList()))
    val uiState: StateFlow<ProfileHomeUiState> get() = _uiState.asStateFlow()

    private val _openPageFlow = MutableSharedFlow<Screen>()
    val openPageFlow = _openPageFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            statusProvider.accountManager
                .getAllAccountFlow()
                .map { list -> list.groupBy(LoggedAccount::platform).map { it.key to it.value } }
                .collect { list ->
                    _uiState.update {
                        it.copy(accountDataList = list)
                    }
                }
        }
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
                ?.let { KRouter.route<Screen>(it) }
                ?.let { _openPageFlow.emit(it) }
        }
    }
}
