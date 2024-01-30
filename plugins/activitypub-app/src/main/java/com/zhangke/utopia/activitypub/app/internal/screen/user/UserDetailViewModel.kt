package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = UserDetailViewModel.Factory::class)
class UserDetailViewModel @AssistedInject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val baseUrlManager: BaseUrlManager,
    private val clientManager: ActivityPubClientManager,
    @Assisted val userUri: FormalUri,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(uri: FormalUri): UserDetailViewModel
    }

    private val _uiState = MutableStateFlow(
        UserDetailUiState(
            account = null,
            relationship = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    init {
        launchInViewModel {
            val userInsight = userUriTransformer.parse(userUri)
            if (userInsight == null) {
                _messageFlow.emit(textOf("Invalid user uri: $userUri"))
                return@launchInViewModel
            }
            val accountRepo = clientManager.getClient(baseUrlManager.decideBaseUrl(userInsight.baseUrl))
                .accountRepo
            val accountResult = accountRepo.lookup(userInsight.webFinger.toString())
            if (accountResult.isFailure) {
                _messageFlow.emit(textOf("Failed to lookup user, because ${accountResult.exceptionOrNull()!!.message}"))
                return@launchInViewModel
            }
            val account = accountResult.getOrThrow()!!
            _uiState.value = _uiState.value.copy(
                account = account
            )
            val relationshipEntityResult = accountRepo.getRelationships(listOf(account.id))
            if (relationshipEntityResult.isFailure) {
                _messageFlow.emit(textOf(accountResult.exceptionOrNull()!!.message.orEmpty()))
                return@launchInViewModel
            }
            val relationshipEntity = relationshipEntityResult.getOrThrow().firstOrNull()
            if (relationshipEntity == null) {
                _messageFlow.emit(textOf("Failed to get relationship entity"))
                return@launchInViewModel
            }
            _uiState.value = _uiState.value.copy(
                relationship = relationshipEntity
            )
        }
    }

    fun onFollowClick() {
    }

    fun onUnfollowClick() {
    }

    fun onAcceptClick() {
    }

    fun onRejectClick() {
    }
}
