package com.zhangke.fread.bluesky.internal.screen.user.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.ProfileViewDetailed
import app.bsky.actor.ViewerState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.UpdateBlockUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipType
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.common.RelationshipUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class BskyUserDetailViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val updateRelationship: UpdateRelationshipUseCase,
    private val updateBlock: UpdateBlockUseCase,
    @Assisted private val role: IdentityRole,
    @Assisted private val did: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
            did: String,
        ): BskyUserDetailViewModel
    }

    private val _uiState = MutableStateFlow(BskyUserDetailUiState.default(did = did))
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage

    init {
        _uiState.update { it.copy(tabs = createTabs()) }
        loadUserDetail()
    }

    private fun loadUserDetail(showLoading: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = showLoading) }
            val client = clientManager.getClient(role)
            _uiState.update { it.copy(isOwner = client.loggedAccountProvider()?.did == did) }
            client.getProfileCatching(GetProfileQueryParams(Did(did)))
                .onSuccess { detailed ->
                    _uiState.update { state ->
                        detailed.updateUiState(state).copy(
                            loading = false,
                            loadError = null,
                        )
                    }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(
                            loading = false,
                            loadError = if (showLoading) it else null,
                        )
                    }
                }
        }
    }

    fun onFollowClick() {
        launchInViewModel {
            updateRelationship(
                role = role,
                targetDid = did,
                type = UpdateRelationshipType.FOLLOW,
            ).handleAndRefresh()
        }
    }

    fun onUnfollowClick() {
        launchInViewModel {
            updateRelationship(
                role = role,
                targetDid = did,
                type = UpdateRelationshipType.UNFOLLOW,
                followUri = uiState.value.followUri,
            ).handleAndRefresh()
        }
    }

    fun onBlockClick() {
        launchInViewModel {
            updateBlock(
                role = role,
                did = did,
                block = true,
                blockUri = null,
            ).handleAndRefresh()
        }
    }

    fun onUnblockClick() {
        launchInViewModel {
            updateBlock(
                role = role,
                did = did,
                block = false,
                blockUri = uiState.value.blockUri,
            ).handleAndRefresh()
        }
    }

    fun onMuteClick(mute: Boolean) {
        launchInViewModel {
            val client = clientManager.getClient(role)
            val did = Did(did)
            val result = if (mute) {
                client.muteActorCatching(did)
            } else {
                client.unmuteActorCatching(did)
            }
            result.handleAndRefresh()
        }
    }

    private suspend fun <T> Result<T>.handleAndRefresh() {
        if (isSuccess) {
            loadUserDetail(false)
        } else {
            _snackBarMessage.emitTextMessageFromThrowable(exceptionOrThrow())
        }
    }

    private fun ProfileViewDetailed.updateUiState(uiState: BskyUserDetailUiState): BskyUserDetailUiState {
        return uiState.copy(
            displayName = this.displayName,
            description = this.description,
            handle = this.handle.handle,
            avatar = this.avatar?.uri,
            banner = this.banner?.uri,
            followsCount = this.followsCount,
            followersCount = this.followersCount,
            postsCount = this.postsCount,
            userHomePageUrl = "https://bsky.app/profile/${this.handle}",
            followUri = this.viewer?.following?.atUri,
            muted = this.viewer?.muted == true,
            blockUri = this.viewer?.blocking?.atUri,
            relationship = this.viewer?.relationship ?: RelationshipUiState.UNKNOWN,
        )
    }

    private val ViewerState.relationship: RelationshipUiState
        get() {
            return when {
                this.blocking != null -> RelationshipUiState.BLOCKING
                this.blockedBy == true -> RelationshipUiState.BLOCKED_BY
                this.following != null -> RelationshipUiState.FOLLOWING
                this.followedBy != null -> RelationshipUiState.FOLLOWED_BY
                else -> RelationshipUiState.UNKNOWN
            }
        }

    private fun createTabs(): List<BlueskyFeeds> {
        return listOf(
            BlueskyFeeds.UserPosts(did),
            BlueskyFeeds.UserReplies(did),
            BlueskyFeeds.UserMedias(did),
            BlueskyFeeds.UserLikes(did),
        )
    }
}
