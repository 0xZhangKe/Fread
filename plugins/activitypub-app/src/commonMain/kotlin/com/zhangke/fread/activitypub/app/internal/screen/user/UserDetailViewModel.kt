package com.zhangke.fread.activitypub.app.internal.screen.user

import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.activitypub.app.internal.push.PushManager
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserDetailViewModel(
    private val accountManager: ActivityPubAccountManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val userUriTransformer: UserUriTransformer,
    private val clientManager: ActivityPubClientManager,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val pushManager: PushManager,
    val role: IdentityRole,
    val userUri: FormalUri?,
    val webFinger: WebFinger?,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(
        UserDetailUiState(
            role = role,
            loading = false,
            userInsight = null,
            accountUiState = null,
            relationship = null,
            domainBlocked = false,
            isAccountOwner = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    init {
        launchInViewModel {
            val webFinger = userUri?.let(userUriTransformer::parse)?.webFinger ?: webFinger
            if (webFinger == null) {
                _messageFlow.emit(textOf("Invalid user."))
                return@launchInViewModel
            }
            _uiState.update { it.copy(loading = true) }
            val accountRepo = clientManager.getClient(role).accountRepo
            val accountResult = lookupAccount(accountRepo, webFinger)
            if (accountResult.isFailure) {
                _uiState.update { it.copy(loading = false) }
                _messageFlow.emit(textOf("Failed to lookup ${webFinger}, ${accountResult.exceptionOrNull()!!.message}"))
                return@launchInViewModel
            }
            val account = accountResult.getOrThrow()!!
            pushManager.subscribe(role, account.id)
            val userInsight = accountEntityAdapter.toUri(account).let(userUriTransformer::parse)!!
            val isAccountOwner = accountManager.getAllLoggedAccount()
                .any { loggedAccount ->
                    loggedAccount.uri == userInsight.uri
                }
            _uiState.value = _uiState.value.copy(
                userInsight = userInsight,
                isAccountOwner = isAccountOwner,
                accountUiState = account.toAccountUiState(),
                loading = false,
            )
            loadRelationship(accountRepo, account.id)
            loadDomainBlockState(accountRepo, userInsight)
        }
    }

    private suspend fun lookupAccount(
        accountRepo: AccountsRepo,
        webFinger: WebFinger,
    ): Result<ActivityPubAccountEntity?> {
        val resultOfWebFinger = accountRepo.lookup(webFinger.toString())
        if (resultOfWebFinger.isSuccess) {
            return resultOfWebFinger
        }
        return accountRepo.lookup("@${webFinger.name}")
    }

    private suspend fun loadRelationship(
        accountRepo: AccountsRepo,
        accountId: String,
    ) {
        val relationshipEntityResult = accountRepo.getRelationships(listOf(accountId))
        if (relationshipEntityResult.isFailure) {
            return
        }
        val relationshipEntity = relationshipEntityResult.getOrThrow().firstOrNull() ?: return
        _uiState.value = _uiState.value.copy(
            relationship = relationshipEntity
        )
    }

    private suspend fun loadDomainBlockState(
        accountRepo: AccountsRepo,
        userUriInsights: UserUriInsights,
    ) {
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
            val accountRepo = clientManager.getClient(role).accountRepo
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
            val accountRepo = clientManager.getClient(role).accountRepo
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

    fun onNewNoteSet(newNote: String) {
        val privateNote = uiState.value.relationship?.note
        if (newNote == privateNote) return
        val accountId = uiState.value.accountUiState?.account?.id ?: return
        launchInViewModel {
            val accountRepo = clientManager.getClient(role).accountRepo
            accountRepo.updateNote(accountId, newNote)
                .onSuccess { relationship ->
                    _uiState.update { it.copy(relationship = relationship) }
                }.onFailure {
                    _messageFlow.emitTextMessageFromThrowable(it)
                }
        }
    }

    fun onMuteUserClick() {
        muteOrUnmute(true)
    }

    fun onUnmuteUserClick() {
        muteOrUnmute(false)
    }

    private fun muteOrUnmute(mute: Boolean) {
        val accountId = uiState.value.accountUiState?.account?.id ?: return
        launchInViewModel {
            val accountRepo = clientManager.getClient(role).accountRepo
            if (mute) {
                accountRepo.mute(accountId)
            } else {
                accountRepo.unmute(accountId)
            }.onSuccess { relationship ->
                _uiState.update { it.copy(relationship = relationship) }
            }.onFailure {
                _messageFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    private fun performRelationshipAction(
        action: suspend (accountsRepo: AccountsRepo, accountId: String) -> Result<ActivityPubRelationshipEntity>,
    ) {
        val accountId = _uiState.value.accountUiState?.account?.id ?: return
        launchInViewModel {
            val accountRepo = clientManager.getClient(role).accountRepo
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

    private fun ActivityPubAccountEntity.toAccountUiState(): UserDetailAccountUiState {
        val customEmojis = emojis.map(emojiEntityAdapter::toEmoji)
        return UserDetailAccountUiState(
            account = this,
            userName = buildRichText(
                document = displayName,
                emojis = customEmojis,
            ),
            description = buildRichText(
                document = note,
                emojis = customEmojis,
                parsePossibleHashtag = true,
            ),
        )
    }
}
