package com.zhangke.fread.bluesky.internal.screen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.ProfileViewDetailed
import app.bsky.actor.ViewerState
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.common.RelationshipUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class BskyUserDetailViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
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

    init {
        _uiState.update { it.copy(tabs = createTabs()) }
        loadUserDetail()
    }

    private fun loadUserDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            clientManager.getClient(role)
                .getProfileCatching(GetProfileQueryParams(Did(did)))
                .onSuccess { detailed ->
                    _uiState.update { state ->
                        detailed.updateUiState(state)
                            .copy(
                                loading = false,
                                loadError = null,
                            )
                    }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(
                            loading = false,
                            loadError = it,
                        )
                    }
                }
        }
    }

    fun onFollowClick() {

    }

    fun onUnfollowClick() {

    }

    fun onUnblockClick() {

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
