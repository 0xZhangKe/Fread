package com.zhangke.fread.bluesky.internal.screen.user.edit

import androidx.lifecycle.ViewModel
import app.bsky.actor.Profile
import com.atproto.repo.GetRecordQueryParams
import com.atproto.repo.GetRecordResponse
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.selfRkey
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class EditProfileViewModel @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val clientManager: BlueskyClientManager,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(role: IdentityRole): EditProfileViewModel
    }

    private val _uiState = MutableStateFlow(EditProfileUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage

    private var profile: Profile? = null

    init {
        loadLoggedUser()
    }

    private fun loadLoggedUser() {
        launchInViewModel {
            val account = accountManager.getAccount(role)
            if (account == null) {
                _snackBarMessage.emit(textOf("Account not found"))
                return@launchInViewModel
            }
            _uiState.update {
                it.copy(
                    userName = account.userName,
                    description = account.description,
                    avatar = account.avatar.orEmpty(),
                    banner = account.banner.orEmpty(),
                )
            }
            getProfile(account.did)
        }
    }

    fun uploadAvatar() {
        launchInViewModel {
            val client = clientManager.getClient(role)
            client.putRecordCatching()
            client.getRecordCatching()
        }
    }

    fun updateProfile() {
        val account = _uiState.value.loggedAccount ?: return
        launchInViewModel {
            var profile = profile
            if (profile == null) {
                val result = getProfile(account.did)
                if (result.isFailure) {
                    _snackBarMessage.emitTextMessageFromThrowable(result.exceptionOrThrow())
                    return@launchInViewModel
                }
                profile = result.getOrThrow()
            }
            val client = clientManager.getClient(role)
            profile.copy(
                displayName = _uiState.value.userName,
                description = _uiState.value.description,
                avatar = _uiState.value.avatar,
                banner = _uiState.value.banner,
            )
            client.putRecordCatching()
        }
    }

    private suspend fun getProfile(didText: String): Result<Profile> {
        val client = clientManager.getClient(role)
        val did = Did(didText)
        return client.getRecordCatching(
            GetRecordQueryParams(
                repo = did, collection = BskyCollections.profile, rkey = selfRkey,
            )
        ).map<Profile, GetRecordResponse> { it.bskyJson() }.onSuccess { profile = it }
    }
}
