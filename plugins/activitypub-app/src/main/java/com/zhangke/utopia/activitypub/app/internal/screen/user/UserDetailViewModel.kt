package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
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
import kotlinx.coroutines.flow.update

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
            userInsight = null,
            account = null,
            relationship = null,
            domainBlocked = false,
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
            _uiState.value = _uiState.value.copy(
                userInsight = userInsight
            )
            val accountRepo =
                clientManager.getClient(baseUrlManager.decideBaseUrl(userInsight.baseUrl))
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
            loadRelationship(accountRepo, account.id)
            loadDomainBlockState(accountRepo, userInsight)
        }
    }

    private suspend fun loadRelationship(
        accountRepo: AccountsRepo,
        accountId: String,
    ){
        val relationshipEntityResult = accountRepo.getRelationships(listOf(accountId))
        if (relationshipEntityResult.isFailure) {
            relationshipEntityResult.exceptionOrNull()?.message?.let {
                _messageFlow.emit(textOf(it))
            }
            return
        }
        val relationshipEntity = relationshipEntityResult.getOrThrow().firstOrNull()
        if (relationshipEntity == null) {
            _messageFlow.emit(textOf("Failed to get relationship entity"))
            return
        }
        _uiState.value = _uiState.value.copy(
            relationship = relationshipEntity
        )
    }

    private suspend fun loadDomainBlockState(
        accountRepo: AccountsRepo,
        userUriInsights: UserUriInsights,
    ){
        val blockedDomainList = accountRepo.getDomainBlocks().getOrNull() ?: return
        val domainBlocked = blockedDomainList.firstOrNull { it == userUriInsights.baseUrl.host }
        _uiState.value = _uiState.value.copy(
            domainBlocked = domainBlocked != null
        )
    }

    fun onFollowClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.follow(accountId)
        }
    }

    fun onUnfollowClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.unfollow(accountId)
        }
    }

    fun onAcceptClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.authorizeFollowRequest(accountId)
        }
    }

    fun onRejectClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.rejectFollowRequest(accountId)
        }
    }

    fun onCancelFollowRequestClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.unfollow(accountId)
        }
    }

    fun onBlockClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.block(accountId)
        }
    }

    fun onUnblockClick() {
        performRelationshipAction { accountsRepo, accountId ->
            accountsRepo.unblock(accountId)
        }
    }

    fun onBlockDomainClick() {
        val userUriInsights = _uiState.value.userInsight ?: return
        launchInViewModel {
            val baseUrl = baseUrlManager.decideBaseUrl(userUriInsights.baseUrl)
            val accountRepo = clientManager.getClient(baseUrl).accountRepo
            accountRepo.blockDomain(userUriInsights.baseUrl.host)
                .onFailure { e ->
                    e.message?.let {
                        _messageFlow.emit(textOf(it))
                    }
                }.onSuccess {
                    _uiState.update {
                        it.copy(domainBlocked = true)
                    }
                }
        }
    }

    fun onUnblockDomainClick() {
        val userUriInsights = _uiState.value.userInsight ?: return
        launchInViewModel {
            val baseUrl = baseUrlManager.decideBaseUrl(userUriInsights.baseUrl)
            val accountRepo = clientManager.getClient(baseUrl).accountRepo
            accountRepo.unblockDomain(userUriInsights.baseUrl.host)
                .onFailure { e ->
                    e.message?.let {
                        _messageFlow.emit(textOf(it))
                    }
                }.onSuccess {
                    _uiState.update {
                        it.copy(domainBlocked = false)
                    }
                }
        }
    }

    private fun performRelationshipAction(
        action: suspend (accountsRepo: AccountsRepo, accountId: String) -> Result<ActivityPubRelationshipEntity>,
    ) {
        val accountId = _uiState.value.account?.id ?: return
        val userUriInsights = _uiState.value.userInsight ?: return
        launchInViewModel {
            val baseUrl = baseUrlManager.decideBaseUrl(userUriInsights.baseUrl)
            val accountRepo = clientManager.getClient(baseUrl).accountRepo
            action(accountRepo, accountId)
                .onFailure { e ->
                    e.message?.let {
                        _messageFlow.emit(textOf(it))
                    }
                }.onSuccess { relationship ->
                    _uiState.update {
                        it.copy(relationship = relationship)
                    }
                }
        }
    }
}
