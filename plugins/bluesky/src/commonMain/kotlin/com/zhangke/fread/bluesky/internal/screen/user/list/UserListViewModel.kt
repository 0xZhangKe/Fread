package com.zhangke.fread.bluesky.internal.screen.user.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.ProfileView
import app.bsky.feed.GetLikesQueryParams
import app.bsky.feed.GetRepostedByQueryParams
import app.bsky.graph.GetBlocksQueryParams
import app.bsky.graph.GetFollowersQueryParams
import app.bsky.graph.GetFollowsQueryParams
import app.bsky.graph.GetMutesQueryParams
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.UpdateBlockUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipType
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipUseCase
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Did

class UserListViewModel(
    private val clientManager: BlueskyClientManager,
    private val accountAdapter: BlueskyAccountAdapter,
    private val updateRelationship: UpdateRelationshipUseCase,
    private val updateBlock: UpdateBlockUseCase,
    private val locator: PlatformLocator,
    private val type: UserListType,
    private val postUri: String?,
    userDid: String?,
) : ViewModel() {

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage

    private val loadController = CommonLoadableController<UserListItemUiState>(
        viewModelScope,
        onPostSnackMessage = { _snackBarMessage.emitInViewModel(it) },
    )

    val uiState: StateFlow<CommonLoadableUiState<UserListItemUiState>> get() = loadController.uiState

    private var cursor: String? = null

    private val userDid: Did? = userDid?.let { Did(it) }

    init {
        loadController.initData(
            getDataFromLocal = { emptyList() },
            getDataFromServer = { getDataFromServer(null) },
        )
    }

    fun onRefresh() {
        loadController.onRefresh { getDataFromServer(null) }
    }

    fun onLoadMore() {
        loadController.onLoadMore { getDataFromServer() }
    }

    fun onFollowClick(user: UserListItemUiState) {
        launchInViewModel {
            updateRelationship(
                locator = locator,
                targetDid = user.did,
                type = UpdateRelationshipType.FOLLOW,
                followUri = user.followingUri,
            ).handleError()
                .onSuccess { uri ->
                    updateUserInfo(user.did) {
                        it.copy(followingUri = uri?.atUri)
                    }
                }
        }
    }

    fun onUnfollowClick(user: UserListItemUiState) {
        launchInViewModel {
            updateRelationship(
                locator = locator,
                targetDid = user.did,
                type = UpdateRelationshipType.UNFOLLOW,
                followUri = user.followingUri,
            ).handleError()
                .onSuccess {
                    updateUserInfo(user.did) {
                        it.copy(followingUri = null)
                    }
                }
        }
    }

    fun onMuteClick(user: UserListItemUiState) {
        launchInViewModel {
            clientManager.getClient(locator)
                .muteActorCatching(Did(user.did))
                .handleError()
                .onSuccess {
                    updateUserInfo(user.did) {
                        it.copy(muted = true)
                    }
                }
        }
    }

    fun onUnmuteClick(user: UserListItemUiState) {
        launchInViewModel {
            clientManager.getClient(locator)
                .unmuteActorCatching(Did(user.did))
                .handleError()
                .onSuccess {
                    updateUserInfo(user.did) {
                        it.copy(muted = false)
                    }
                }
        }
    }

    fun onBlockClick(user: UserListItemUiState) {
        launchInViewModel {
            updateBlock(
                locator = locator,
                did = user.did,
                block = true,
                blockUri = user.blockUri,
            ).handleError()
                .onSuccess { uri ->
                    updateUserInfo(user.did) {
                        it.copy(blockUri = uri?.atUri)
                    }
                }
        }
    }

    fun onUnblockClick(user: UserListItemUiState) {
        launchInViewModel {
            updateBlock(
                locator = locator,
                did = user.did,
                block = false,
                blockUri = user.blockUri,
            ).handleError()
                .onSuccess { uri ->
                    updateUserInfo(user.did) {
                        it.copy(blockUri = uri?.atUri)
                    }
                }
        }
    }

    private fun updateUserInfo(did: String, block: (UserListItemUiState) -> UserListItemUiState) {
        loadController.mutableUiState.update { state ->
            state.copy(
                dataList = state.dataList.map {
                    if (it.did == did) {
                        block(it)
                    } else {
                        it
                    }
                },
            )
        }
    }

    private suspend fun <T> Result<T>.handleError(): Result<T> {
        return this.onFailure {
            _snackBarMessage.emitTextMessageFromThrowable(it)
        }
    }

    private suspend fun getDataFromServer(cursor: String? = this.cursor): Result<List<UserListItemUiState>> {
        val client = clientManager.getClient(locator)
        val pagedDataResult = when (type) {
            UserListType.LIKE -> {
                if (postUri == null) return Result.failure(IllegalStateException("PostUri is null"))
                client.getLikesCatching(
                    GetLikesQueryParams(
                        uri = AtUri(postUri),
                        cursor = cursor
                    )
                )
            }

            UserListType.REBLOG -> {
                if (postUri == null) return Result.failure(IllegalStateException("PostUri is null"))
                client.getRepostedCatching(
                    GetRepostedByQueryParams(uri = AtUri(postUri), cursor = cursor)
                )
            }

            UserListType.FOLLOWERS -> {
                val did = userDid ?: client.loggedAccountProvider()?.did?.let { Did(it) }
                if (did == null) return Result.success(emptyList())
                client.getFollowersCatching(
                    GetFollowersQueryParams(
                        actor = did,
                        cursor = cursor
                    )
                )
            }

            UserListType.FOLLOWING -> {
                val did = userDid ?: client.loggedAccountProvider()?.did?.let { Did(it) }
                if (did == null) return Result.success(emptyList())
                client.getFollowsCatching(GetFollowsQueryParams(actor = did, cursor = cursor))
            }

            UserListType.MUTED -> {
                client.getMutesCatching(GetMutesQueryParams(cursor = cursor))
            }

            UserListType.BLOCKED -> {
                client.getBlocksCatching(GetBlocksQueryParams(cursor = cursor))
            }
        }
        return pagedDataResult.map { data ->
            this.cursor = data.cursor
            data.list.map { convertToUiState(it) }
        }
    }

    private fun convertToUiState(profile: ProfileView): UserListItemUiState {
        return UserListItemUiState(
            author = accountAdapter.convertToBlogAuthor(profile),
            did = profile.did.did,
            followingUri = profile.viewer?.following?.atUri,
            followedBy = !profile.viewer?.followedBy?.atUri.isNullOrEmpty(),
            blockUri = profile.viewer?.blocking?.atUri,
            muted = profile.viewer?.muted == true,
        )
    }
}
