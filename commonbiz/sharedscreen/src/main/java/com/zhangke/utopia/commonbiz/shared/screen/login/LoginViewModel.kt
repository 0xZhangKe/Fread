package com.zhangke.utopia.commonbiz.shared.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.status.usecase.QueryAllPlatformByLocalSourceUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val queryAllPlatform: QueryAllPlatformByLocalSourceUseCase,
) : ViewModel() {

    lateinit var defaultBlogPlatform: List<BlogPlatform>

    private val _uiState = MutableStateFlow(LoginUiState(emptyList()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPrepared() {
        _uiState.update { it.copy(platformList = defaultBlogPlatform) }
        viewModelScope.launch {
            queryAllPlatform()
                .onSuccess { platformList ->
                    val newPlatformList = defaultBlogPlatform.plus(platformList)
                        .distinctBy { it.uri }
                    _uiState.update { it.copy(platformList = newPlatformList) }
                }.onFailure {

                }
        }
    }

    fun onServerHostConfirmClick(url: FormalBaseUrl) {
        launchInViewModel {
            statusProvider.accountManager
                .launchAuthBySource(url)
        }
    }
}
