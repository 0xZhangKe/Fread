package com.zhangke.fread.bluesky.internal.screen.user.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.ProfileViewDetailed
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.RefreshSessionUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateBlockUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipType
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.framework.unknown_error
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
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
    private val accountAdapter: BlueskyAccountAdapter,
    private val updateRelationship: UpdateRelationshipUseCase,
    private val updateBlock: UpdateBlockUseCase,
    private val accountManager: BlueskyLoggedAccountManager,
    private val refreshSession: RefreshSessionUseCase,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val did: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            locator: PlatformLocator,
            did: String,
        ): BskyUserDetailViewModel
    }

    private val _uiState = MutableStateFlow(BskyUserDetailUiState.default(did = did))
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage

    private var loadJob: Job? = null
    private var sessionRefreshed = false

    init {
        loadUserDetail()
    }

    fun onPageResume() {
        if (loadJob?.isActive == true) return
        loadUserDetail()
    }

    private fun loadUserDetail(showLoading: Boolean = true) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = showLoading) }
            val client = clientManager.getClient(locator)
            val isOwner = client.loggedAccountProvider()?.did == did
            _uiState.update { it.copy(isOwner = isOwner, tabs = createTabs(isOwner)) }
            client.getProfileCatching(GetProfileQueryParams(Did(did)))
                .onSuccess { detailed ->
                    _uiState.update { state ->
                        detailed.updateUiState(state).copy(
                            loading = false,
                            loadError = null,
                        )
                    }
                    if (isOwner) {
                        accountManager.updateAccountProfile(locator, detailed)
                        if (!sessionRefreshed) {
                            refreshSession()
                            sessionRefreshed = true
                        }
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
                locator = locator,
                targetDid = did,
                type = UpdateRelationshipType.FOLLOW,
            ).handleAndRefresh()
        }
    }

    fun onUnfollowClick() {
        launchInViewModel {
            updateRelationship(
                locator = locator,
                targetDid = did,
                type = UpdateRelationshipType.UNFOLLOW,
                followUri = uiState.value.followUri,
            ).handleAndRefresh()
        }
    }

    fun onBlockClick() {
        launchInViewModel {
            updateBlock(
                locator = locator,
                did = did,
                block = true,
                blockUri = null,
            ).handleAndRefresh()
        }
    }

    fun onUnblockClick() {
        launchInViewModel {
            updateBlock(
                locator = locator,
                did = did,
                block = false,
                blockUri = uiState.value.blockUri,
            ).handleAndRefresh()
        }
    }

    fun onMuteClick(mute: Boolean) {
        launchInViewModel {
            val client = clientManager.getClient(locator)
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
            val errorMessage = exceptionOrNull()?.toTextStringOrNull()
            _snackBarMessage.emit(
                errorMessage ?: textOf(com.zhangke.fread.framework.Res.string.unknown_error)
            )
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
            relationship = this.viewer?.let { accountAdapter.convertRelationship(it) },
        )
    }

    private fun createTabs(isOwner: Boolean): List<BlueskyFeeds> {
        return mutableListOf<BlueskyFeeds>().apply {
            add(BlueskyFeeds.UserPosts(did))
            add(BlueskyFeeds.UserReplies(did))
            add(BlueskyFeeds.UserMedias(did))
            if (isOwner) {
                add(BlueskyFeeds.UserLikes(did))
            }
        }
    }
}
