package com.zhangke.fread.bluesky.internal.screen.user.edit

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import app.bsky.actor.Profile
import com.atproto.repo.GetRecordQueryParams
import com.atproto.repo.GetRecordResponse
import com.atproto.repo.PutRecordRequest
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.selfRkey
import com.zhangke.fread.bluesky.internal.usecase.UploadBlobUseCase
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.model.Blob

class EditProfileViewModel @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val clientManager: BlueskyClientManager,
    private val uploadBlob: UploadBlobUseCase,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(role: IdentityRole): EditProfileViewModel
    }

    private val _uiState = MutableStateFlow(EditProfileUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage

    private val _finishScreenFlow = MutableSharedFlow<Unit>()
    val finishScreenFlow = _finishScreenFlow.asSharedFlow()

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
                    loggedAccount = account,
                    userName = TextFieldValue(account.userName),
                    description = TextFieldValue(account.description),
                    avatar = account.avatar.orEmpty(),
                    banner = account.banner.orEmpty(),
                )
            }
            getProfile(account.did)
        }
    }

    fun onBannerSelected(uri: PlatformUri) {
        _uiState.update { it.copy(bannerLocalUri = uri) }
    }

    fun onAvatarSelected(uri: PlatformUri) {
        _uiState.update { it.copy(avatarLocalUri = uri) }
    }

    fun onSaveClick() {
        updateProfile()
    }

    fun onUserNameChanged(text: TextFieldValue) {
        _uiState.update { it.copy(userName = text) }
    }

    fun onDescriptionChanged(text: TextFieldValue) {
        _uiState.update { it.copy(description = text) }
    }

    private fun updateProfile() {
        val account = _uiState.value.loggedAccount ?: return
        launchInViewModel {
            _uiState.update { it.copy(requesting = true) }
            val currentUiState = uiState.value
            var profile = profile
            if (profile == null) {
                val result = getProfile(account.did)
                if (result.isFailure) {
                    _uiState.update { it.copy(requesting = false) }
                    _snackBarMessage.emitTextMessageFromThrowable(result.exceptionOrThrow())
                    return@launchInViewModel
                }
                profile = result.getOrThrow()
            }
            val uploadResult =
                uploadImages(currentUiState.avatarLocalUri, currentUiState.bannerLocalUri)
            if (uploadResult.isFailure) {
                _uiState.update { it.copy(requesting = false) }
                _snackBarMessage.emitTextMessageFromThrowable(uploadResult.exceptionOrThrow())
                return@launchInViewModel
            }
            val (avatar, banner) = uploadResult.getOrThrow()
            val client = clientManager.getClient(role)
            profile = profile.copy(
                displayName = _uiState.value.userName.text,
                description = _uiState.value.description.text,
                avatar = avatar ?: profile.avatar,
                banner = banner ?: profile.banner,
            )
            client.putRecordCatching(
                PutRecordRequest(
                    repo = Did(account.did),
                    collection = BskyCollections.profile,
                    rkey = selfRkey,
                    record = profile.bskyJson(),
                )
            ).onFailure { t ->
                _uiState.update { it.copy(requesting = false) }
                _snackBarMessage.emitTextMessageFromThrowable(t)
            }.onSuccess {
                _uiState.update { it.copy(requesting = false) }
                _finishScreenFlow.emit(Unit)
            }
        }
    }

    private suspend fun uploadImages(
        avatar: PlatformUri?,
        banner: PlatformUri?,
    ): Result<Pair<Blob?, Blob?>> {
        if (avatar == null && banner == null) return Result.success(null to null)
        if (avatar == null) return uploadBlob(role, banner!!).map { null to it }
        if (banner == null) return uploadBlob(role, avatar).map { it to null }
        return supervisorScope {
            val avatarDeferred = async { uploadBlob(role, avatar) }
            val bannerDeferred = async { uploadBlob(role, banner) }
            val avatarResult = avatarDeferred.await()
            val bannerResult = bannerDeferred.await()
            if (avatarResult.isFailure || bannerResult.isFailure) {
                val exception = avatarResult.exceptionOrNull() ?: bannerResult.exceptionOrThrow()
                return@supervisorScope Result.failure(exception)
            }
            Result.success(avatarResult.getOrThrow() to bannerResult.getOrThrow())
        }
    }

    private suspend fun getProfile(didText: String): Result<Profile> {
        val client = clientManager.getClient(role)
        val did = Did(didText)
        return client.getRecordCatching(
            GetRecordQueryParams(
                repo = did, collection = BskyCollections.profile, rkey = selfRkey,
            )
        ).map<Profile, GetRecordResponse> { it.value.bskyJson() }.onSuccess { profile = it }
    }
}
