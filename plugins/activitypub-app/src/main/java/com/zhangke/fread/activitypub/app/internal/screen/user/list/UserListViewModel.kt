package com.zhangke.fread.activitypub.app.internal.screen.user.list

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.api.PagingResult
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = UserListViewModel.Factory::class)
class UserListViewModel @AssistedInject constructor(
    private val clientManager: ActivityPubClientManager,
    private val userUriTransformer: UserUriTransformer,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    @Assisted private val role: IdentityRole,
    @Assisted private val type: UserListType,
    @Assisted private val statusId: String?,
    @Assisted private val userUri: FormalUri?,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(
            role: IdentityRole,
            type: UserListType,
            statusId: String?,
            userUri: FormalUri?,
        ): UserListViewModel
    }

    private val _uiState = MutableStateFlow(
        UserListUiState(
            type = type,
            role = role,
            loading = false,
            userList = emptyList(),
            loadMoreState = LoadState.Idle,
        )
    )
    val uiState: StateFlow<UserListUiState> = _uiState

    private val mutableSnackMessageFlow = MutableSharedFlow<TextString>()
    val snackMessageFlow = mutableSnackMessageFlow.asSharedFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    private var nextMaxId: String? = null

    private var userId: String? = null

    init {
        loadFirstPageUsers()
    }

    fun onRefresh() {
        loadFirstPageUsers()
    }

    fun onLoadMore() {
        loadNextPageUsers()
    }

    private fun loadFirstPageUsers() {
        if (refreshJob?.isActive == true) return
        loadMoreJob?.cancel()
        _uiState.update { it.copy(loading = true) }
        refreshJob = launchInViewModel {
            fetchUserListFromServer()
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            loading = false,
                            userList = it,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(loading = false) }
                    mutableSnackMessageFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    private fun loadNextPageUsers() {
        if (refreshJob?.isActive == true) return
        if (loadMoreJob?.isActive == true) return
        if (nextMaxId.isNullOrEmpty()) return
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            fetchUserListFromServer(nextMaxId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            loadMoreState = LoadState.Idle,
                            userList = state.userList + it,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
                }
        }
    }

    private suspend fun fetchUserListFromServer(maxId: String? = null): Result<List<BlogAuthor>> {
        val client = clientManager.getClient(role)
        val pagingResult = when (type) {
            UserListType.REBLOGS -> client.statusRepo.getReblogBy(
                statusId = statusId!!,
                maxId = maxId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
            )

            UserListType.FAVOURITES -> client.statusRepo.getFavouritesBy(
                statusId = statusId!!,
                maxId = maxId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
            )

            UserListType.BLOCKED -> client.accountRepo.getBlockedUserList(
                maxId = nextMaxId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
            )

            UserListType.MUTED -> client.accountRepo.getMutedUserList(
                maxId = nextMaxId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
            )

            UserListType.FOLLOWING -> getFollowUserList(maxId)

            UserListType.FOLLOWERS -> getFollowUserList(maxId)
        }
        return pagingResult.map {
            nextMaxId = it.pagingInfo.nextMaxId
            it.data.toAuthors()
        }
    }

    private suspend fun getFollowUserList(maxId: String?): Result<PagingResult<List<ActivityPubAccountEntity>>> {
        val userIdResult = getPageTargetUserId()
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrThrow())
        val userId = userIdResult.getOrThrow()
        val accountRepo = clientManager.getClient(role).accountRepo
        return if (type == UserListType.FOLLOWERS) {
            accountRepo.getFollowers(
                id = userId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            )
        } else {
            accountRepo.getFollowing(
                id = userId,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            )
        }
    }

    fun onUnblockClick(author: BlogAuthor) {
        launchInViewModel {
            val id = getUserIdByUri(author.uri) ?: return@launchInViewModel
            clientManager.getClient(role)
                .accountRepo
                .unblock(id)
                .onFailure {
                    mutableSnackMessageFlow.emitTextMessageFromThrowable(it)
                }.onSuccess {
                    if (type == UserListType.BLOCKED) {
                        _uiState.update { state ->
                            state.copy(
                                userList = state.userList.filterNot { it.uri == author.uri }
                            )
                        }
                    }
                }
        }
    }

    fun onUnmuteClick(author: BlogAuthor) {
        launchInViewModel {
            val accountRepo = clientManager.getClient(role).accountRepo
            val authorId = getUserIdByUri(author.uri) ?: return@launchInViewModel
            accountRepo.unmute(authorId)
                .onSuccess {
                    if (type == UserListType.MUTED) {
                        _uiState.update { state ->
                            state.copy(
                                userList = state.userList.filterNot { it.uri == author.uri }
                            )
                        }
                    }
                }.onFailure {
                    mutableSnackMessageFlow.emitTextMessageFromThrowable(it)
                }
        }
    }

    private suspend fun getPageTargetUserId(): Result<String> {
        if (userUri == null) {
            return Result.failure(IllegalStateException("User uri is null!"))
        }
        val userId = userId ?: getUserIdByUri(userUri)?.also {
            this.userId = it
        }
        if (userId == null) {
            return Result.failure(IllegalStateException("Invalid user uri: $userUri"))
        }
        return Result.success(userId)
    }

    private suspend fun getUserIdByUri(uri: FormalUri): String? {
        val userInsight = userUriTransformer.parse(uri)
        if (userInsight == null) {
            mutableSnackMessageFlow.emit(textOf("Invalid user uri: $uri"))
            return null
        }
        val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(userInsight.webFinger, role)
        if (userIdResult.isFailure) {
            mutableSnackMessageFlow.emitTextMessageFromThrowable(userIdResult.exceptionOrThrow())
            return null
        }
        return userIdResult.getOrNull()
    }

    private fun List<ActivityPubAccountEntity>.toAuthors(): List<BlogAuthor> {
        return this.map { it.toAuthor() }
    }

    private fun ActivityPubAccountEntity.toAuthor(): BlogAuthor {
        return accountEntityAdapter.toAuthor(this)
    }
}
