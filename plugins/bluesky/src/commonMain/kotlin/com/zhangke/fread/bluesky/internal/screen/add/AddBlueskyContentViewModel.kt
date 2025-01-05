package com.zhangke.fread.bluesky.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.usecase.LoginToBskyUseCase
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ViewModelFactory
import kotlinx.coroutines.Job
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
    private val loginToBluesky: LoginToBskyUseCase,
    private val contentRepo: FreadContentRepo,
    private val platformRepo: BlueskyPlatformRepo,
    @Assisted private val baseUrl: FormalBaseUrl,
    @Assisted private val loginMode: Boolean,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            baseUrl: FormalBaseUrl,
            loginMode: Boolean,
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

    private var loggingJob: Job? = null

    fun onHostingChange(hosting: String) {
        _uiState.update { it.copy(hosting = hosting) }
    }

    fun onUserNameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onSkipClick() {
        launchInViewModel {
            saveBlueskyContent()
            _finishPageFlow.emit(Unit)
        }
    }

    fun onLoginClick() {
        if (_uiState.value.logging) return
        if (loggingJob?.isActive == true) return
        val hosting = uiState.value.hosting.trim()
        val username = uiState.value.username.trim()
        val password = uiState.value.password.trim()
        loggingJob = launchInViewModel {
            _uiState.update { it.copy(logging = true) }
            loginToBluesky(hosting, username, password)
                .onSuccess {
                    saveBlueskyContent()
                    _uiState.update { it.copy(logging = false) }
                    _finishPageFlow.emit(Unit)
                }
                .onFailure {
                    _uiState.update { it.copy(logging = false) }
                    _snackBarMessage.emit(textOf(it.message ?: "Login Failed!"))
                }
        }
    }

    fun onCancelLogin() {
        loggingJob?.cancel()
        _uiState.update { it.copy(logging = false) }
    }

    private suspend fun saveBlueskyContent() {
        if (loginMode) return
        val platform = platformRepo.getPlatform(baseUrl)
        val id = platform.baseUrl.toString()
        if (contentRepo.getContent(id) != null) return
        val content = BlueskyContent(
            id = platform.baseUrl.toString(),
            order = contentRepo.getMaxOrder() + 1,
            name = platform.name,
            baseUrl = platform.baseUrl,
            feedsList = emptyList(),
        )
        contentRepo.insertContent(content)
    }
}
