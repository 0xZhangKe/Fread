package com.zhangke.utopia.profile.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.common.feeds.usecase.QueryAllPlatformByLocalSourceUseCase
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val queryAllPlatform: QueryAllPlatformByLocalSourceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(emptyList()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            queryAllPlatform()
                .onSuccess { platformList ->
                    _uiState.update { it.copy(platformList = platformList) }
                }.onFailure {

                }
        }
    }
}
