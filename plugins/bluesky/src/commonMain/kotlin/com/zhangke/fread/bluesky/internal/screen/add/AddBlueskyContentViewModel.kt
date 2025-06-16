package com.zhangke.fread.bluesky.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.usecase.LoginToBskyUseCase
import com.zhangke.fread.bluesky.internal.utils.AtRequestException
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

    fun onFactorTokenChange(factorToken: String) {
        _uiState.update { it.copy(factorToken = factorToken) }
    }

    fun onLoginClick() {
        if (_uiState.value.logging) return
        if (loggingJob?.isActive == true) return
        val hosting = uiState.value.hosting.trim()
        val username = uiState.value.username.trim()
        val password = uiState.value.password.trim()
        val factorToken = uiState.value.factorToken.trim()
        loggingJob = launchInViewModel {
            val baseUrl = FormalBaseUrl.parse(hosting)
            if (baseUrl == null) {
                _snackBarMessage.emit(textOf("Invalid host!"))
                return@launchInViewModel
            }
            _uiState.update { it.copy(logging = true) }
            loginToBluesky(baseUrl, username, password, factorToken)
                .onSuccess {
                    _uiState.update { it.copy(logging = false) }
                    if (!loginMode) {
                        saveBlueskyContent(it)
                    }
                    _finishPageFlow.emit(Unit)
                }
                .onFailure { t ->
                    _uiState.update { it.copy(logging = false) }
                    if (t is AtRequestException) {
                        if (t.needAuthFactorTokenRequired) {
                            _uiState.update { it.copy(authFactorRequired = true) }
                        }
                        if (!t.errorMessage.isNullOrEmpty()) {
                            _snackBarMessage.emit(textOf(t.errorMessage))
                        } else {
                            _snackBarMessage.emit(
                                textOf(t.error ?: t.message ?: "Login Failed! by Bsky Api.")
                            )
                        }
                    } else {
                        _snackBarMessage.emit(
                            textOf(t.message ?: "Login Failed! ${t::class.simpleName}")
                        )
                    }
                }
        }
    }

    fun onCancelLogin() {
        loggingJob?.cancel()
        _uiState.update { it.copy(logging = false) }
    }

    private suspend fun saveBlueskyContent(account: BlueskyLoggedAccount) {
        val content = BlueskyContent(
            order = contentRepo.getMaxOrder() + 1,
            name = account.platform.name,
            baseUrl = account.platform.baseUrl,
            feedsList = emptyList(),
            accountDid = account.did,
        )
        contentRepo.insertContent(content)
    }
}
