package com.zhangke.fread.commonbiz.shared.screen.status.account

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class SelectAccountOpenStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val blogId: String,
    @Assisted private val blogUrl: String,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val protocol: StatusProviderProtocol,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            blogId: String,
            blogUrl: String,
            locator: PlatformLocator,
            protocol: StatusProviderProtocol,
        ): SelectAccountOpenStatusViewModel
    }

    private val _uiState = MutableStateFlow(
        SelectAccountOpenStatusUiState.default(loadingAccounts = true),
    )
    val uiState = _uiState

    init {
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
        _uiState.update { it.copy(searching = true) }
    }

    fun onCancelSearchClick() {
        _uiState.update { it.copy(searching = false) }
    }
}
