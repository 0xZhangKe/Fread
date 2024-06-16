package com.zhangke.fread.activitypub.app.internal.screen.user.follow

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
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
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = FollowViewModel.Factory::class)
class FollowViewModel @AssistedInject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    @Assisted private val role: IdentityRole,
    @Assisted private val userUri: FormalUri,
    @Assisted private val isFollowing: Boolean,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(role: IdentityRole, userUri: FormalUri, isFollowing: Boolean): FollowViewModel
    }

    private val _uiState = MutableStateFlow(
        FollowUiState(
            initializing = false,
            refreshing = false,
            loadMoreState = LoadState.Idle,
            list = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    private var userId: String? = null

    private var nextMaxId: String? = null

    init {
        launchInViewModel {
            val userInsight = userUriTransformer.parse(userUri)
            if (userInsight == null) {
                _messageFlow.emit(textOf("Invalid user uri: $userUri"))
                return@launchInViewModel
            }
            _uiState.update { it.copy(initializing = true) }
            val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(userInsight.webFinger, role)
            if (userIdResult.isFailure) {
                _messageFlow.emitTextMessageFromThrowable(userIdResult.exceptionOrThrow())
                _uiState.update { it.copy(initializing = false) }
                return@launchInViewModel
            }
            userId = userIdResult.getOrThrow()
            loadFollowList(id = userId!!)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            initializing = false,
                            list = list,
                        )
                    }
                }.onFailure { t ->
                    _messageFlow.emitTextMessageFromThrowable(t)
                    _uiState.update { it.copy(initializing = false) }
                }
        }
    }

    fun onRefresh() {
        val userId = userId ?: return
        nextMaxId = null
        launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            loadFollowList(userId).onFailure { t ->
                _uiState.update { it.copy(refreshing = false) }
                _messageFlow.emitTextMessageFromThrowable(t)
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        refreshing = false,
                        list = list,
                    )
                }
            }
        }
    }

    fun onLoadMore() {
        val userId = userId ?: return
        if (_uiState.value.list.isEmpty()) return
        if (nextMaxId.isNullOrEmpty()) return
        launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            loadFollowList(
                id = userId,
            ).onFailure { t ->
                _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        loadMoreState = LoadState.Idle,
                        list = it.list + list,
                    )
                }
            }
        }
    }

    fun onUserInfoClick(accountEntity: ActivityPubAccountEntity) {

    }

    private suspend fun loadFollowList(
        id: String,
    ): Result<List<BlogAuthor>> {
        val repo = clientManager.getClient(role).accountRepo
        return if (isFollowing) {
            repo.getFollowing(
                id = id,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = nextMaxId,
            ).map { result ->
                nextMaxId = result.pagingInfo.nextMaxId
                result.data.map { accountEntityAdapter.toAuthor(it) }
            }
        } else {
            repo.getFollowers(
                id = id,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = nextMaxId,
            ).map { result ->
                nextMaxId = result.pagingInfo.nextMaxId
                result.data.map { accountEntityAdapter.toAuthor(it) }
            }
        }
    }
}
