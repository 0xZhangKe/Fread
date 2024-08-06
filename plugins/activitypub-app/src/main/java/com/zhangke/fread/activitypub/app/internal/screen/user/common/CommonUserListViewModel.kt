package com.zhangke.fread.activitypub.app.internal.screen.user.common

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

abstract class CommonUserListViewModel(
    private val role: IdentityRole,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val userUriTransformer: UserUriTransformer,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
) : ViewModel() {

    protected val _uiState = MutableStateFlow(CommonUserUiState.default())
    val uiState: StateFlow<CommonUserUiState> = _uiState

    protected val _snackMessageFlow = MutableSharedFlow<TextString>()
    val snackMessageFlow = _snackMessageFlow.asSharedFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    abstract suspend fun loadFirstPageUsersFromServer(
        accountRepo: AccountsRepo,
    ): Result<List<ActivityPubAccountEntity>>

    abstract suspend fun loadNextPageUsersFromServer(
        accountRepo: AccountsRepo,
    ): Result<List<ActivityPubAccountEntity>>

    init {
        loadFirstPageUsers()
    }

    fun onRefresh() {
        loadFirstPageUsers()
    }

    open fun onLoadMore() {
        loadNextPageUsers()
    }

    private fun loadFirstPageUsers() {
        if (refreshJob?.isActive == true) return
        _uiState.update { it.copy(loading = true) }
        refreshJob = launchInViewModel {
            val accountRepo = clientManager.getClient(role).accountRepo
            loadFirstPageUsersFromServer(accountRepo)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            loading = false,
                            userList = it.map { it.toAuthor() },
                        )
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(loading = false) }
                    _snackMessageFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    private fun loadNextPageUsers() {
        if (loadMoreJob?.isActive == true) return
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            val accountRepo = clientManager.getClient(role).accountRepo
            loadNextPageUsersFromServer(accountRepo)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            loadMoreState = LoadState.Idle,
                            userList = state.userList + it.map { it.toAuthor() },
                        )
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
                }
        }
    }

    protected suspend fun getAuthorId(author: BlogAuthor): String? {
        val userInsight = userUriTransformer.parse(author.uri)
        if (userInsight == null) {
            _snackMessageFlow.emit(textOf("Invalid user uri: ${author.uri}"))
            return null
        }
        val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(userInsight.webFinger, role)
        if (userIdResult.isFailure) {
            _snackMessageFlow.emitTextMessageFromThrowable(userIdResult.exceptionOrThrow())
            return null
        }
        return userIdResult.getOrNull()
    }

    private fun ActivityPubAccountEntity.toAuthor(): BlogAuthor {
        return accountEntityAdapter.toAuthor(this)
    }
}
