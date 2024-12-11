package com.zhangke.fread.bluesky.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.usecase.LoginToBskyUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class AddBlueskyContentViewModel @Inject constructor(
    private val loginToBsky: LoginToBskyUseCase,
    @Assisted private val baseUrl: FormalBaseUrl,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            baseUrl: FormalBaseUrl,
        ): AddBlueskyContentViewModel
    }

    private val _uiState = MutableStateFlow(
        AddBlueskyContentUiState.default(
            hosting = baseUrl.toString(),
        )
    )
    val uiState: StateFlow<AddBlueskyContentUiState> = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage: SharedFlow<TextString> = _snackBarMessage

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow: SharedFlow<Unit> = _finishPageFlow.asSharedFlow()

    fun onHostingChange(hosting: String) {
        _uiState.update { it.copy(hosting = hosting) }
    }

    fun onUserNameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onLoginClick() {
        if (_uiState.value.logging) return
        val hosting = uiState.value.hosting.trim()
        val username = uiState.value.username.trim()
        val password = uiState.value.password.trim()
        launchInViewModel {
            _uiState.update { it.copy(logging = true) }
            loginToBsky(hosting, username, password)
                .onSuccess {
                    _uiState.update { it.copy(logging = false) }
                    _finishPageFlow.emit(Unit)
                }
                .onFailure {
                    _uiState.update { it.copy(logging = false) }
                    _snackBarMessage.emit(textOf(it.message ?: "Login Failed!"))
                }
        }
    }
}
