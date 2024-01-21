package com.zhangke.utopia.feature.message.screens.home

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class NotificationsHomeViewModel(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsHomeUiState(0, emptyList()))
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
                    _uiState.update {
                        it.copy(currentIndex = 0, accountToTabList = accountToTabList)
                    }
                }
        }
    }
}
