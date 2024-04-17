package com.zhangke.utopia.activitypub.app.internal.screen.status.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.remove
import com.zhangke.framework.collections.removeIndex
import com.zhangke.framework.collections.updateIndex
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.model.CustomEmoji
import com.zhangke.utopia.activitypub.app.internal.model.PostStatusVisibility
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.adapter.CustomEmojiAdapter
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.emoji.GetCustomEmojiUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.media.UploadMediaAttachmentUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.PostStatusUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.uri.FormalUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@HiltViewModel
class PostStatusViewModel @Inject constructor(
    private val getCustomEmoji: GetCustomEmojiUseCase,
    private val emojiAdapter: CustomEmojiAdapter,
    private val accountManager: ActivityPubAccountManager,
    private val uploadMediaAttachment: UploadMediaAttachmentUseCase,
    private val clientManager: ActivityPubClientManager,
    private val postNoAttachmentStatus: PostStatusUseCase,
    private val platformUriTransformer: PlatformUriTransformer,
) : ViewModel() {

    companion object {

        const val MAX_CONTENT = 1000
    }

    var accountUri: FormalUri? = null
    var replyToId: String? = null
    var replyToName: String? = null

    private val _uiState = MutableStateFlow(LoadableState.loading<PostStatusUiState>())
    val uiState: StateFlow<LoadableState<PostStatusUiState>> = _uiState.asStateFlow()

    private val _postState = MutableSharedFlow<LoadableState<Unit>>()
    val postState: SharedFlow<LoadableState<Unit>> = _postState.asSharedFlow()

    fun onPrepared() {
        launchInViewModel {
            val allLoggedAccount = accountManager.getAllLoggedAccount()
            val defaultAccount = if (accountUri != null) {
                allLoggedAccount.firstOrNull { it.uri == accountUri }
                    ?: allLoggedAccount.firstOrNull()
            } else {
                allLoggedAccount.firstOrNull()
            }
            if (defaultAccount == null) {
                _uiState.updateToFailed(IllegalStateException("Not login!"))
            } else {
                _uiState.value = LoadableState.success(
                    PostStatusUiState(
                        account = defaultAccount,
                        availableAccountList = allLoggedAccount,
                        content = "",
                        attachment = null,
                        maxMediaCount = 4,
                        visibility = PostStatusVisibility.PUBLIC,
                        sensitive = false,
                        maxContent = MAX_CONTENT,
                        replyToAuthorName = replyToName,
                        warningContent = "",
                        emojiList = emptyList(),
                        language = Locale.ROOT,
                    )
                )
            }
        }
        configCustomEmoji()
    }

    private fun configCustomEmoji() {
        launchInViewModel {
            _uiState.mapNotNull { it.successDataOrNull()?.account?.platform }
                .distinctUntilChanged()
                .mapNotNull { FormalUri.from(it.uri) }
                .mapNotNull { platformUriTransformer.parse(it) }
                .mapNotNull { getCustomEmoji(it.serverBaseUrl).getOrNull() }
                .map { emojiAdapter.toEmojiCell(7, it) }
                .collect { emojiList ->
                    _uiState.updateOnSuccess {
                        it.copy(emojiList = emojiList)
                    }
                }
        }
    }

    fun onSwitchAccountClick(account: ActivityPubLoggedAccount) {
        _uiState.updateOnSuccess {
            it.copy(account = account)
        }
    }

    fun onContentChanged(inputtedText: String) {
        if (inputtedText.length > MAX_CONTENT) return
        _uiState.updateOnSuccess {
            it.copy(content = inputtedText)
        }
    }

    fun onSensitiveClick() {
        _uiState.updateOnSuccess {
            it.copy(sensitive = !it.sensitive)
        }
    }

    fun onMediaSelected(list: List<Uri>) {
        if (list.isEmpty()) return
        val fileList = list.mapNotNull { it.toContentProviderFile() }
        val videoFile = fileList.firstOrNull { it.isVideo }
        if (videoFile != null) {
            onAddVideo(videoFile)
        } else {
            onAddImageList(fileList)
        }
    }

    private fun onAddVideo(file: ContentProviderFile) {
        val attachmentFile = buildAttachmentFile(file)
        attachmentFile.uploadJob.upload()
        _uiState.updateOnSuccess {
            it.copy(
                attachment = PostStatusAttachment.VideoAttachment(attachmentFile)
            )
        }
    }

    private fun onAddImageList(uriList: List<ContentProviderFile>) {
        val imageList = mutableListOf<PostStatusFile>()
        _uiState.value
            .requireSuccessData()
            .attachment
            ?.asImageAttachmentOrNull
            ?.imageList
            ?.let { imageList += it }
        uriList.forEach { uri ->
            val attachmentFile = buildAttachmentFile(uri)
            attachmentFile.uploadJob.upload()
            imageList += attachmentFile
        }
        _uiState.updateOnSuccess {
            it.copy(attachment = PostStatusAttachment.ImageAttachment(imageList))
        }
    }

    private fun buildUploadFileJob(file: ContentProviderFile) = UploadMediaJob(
        file = file,
        role = IdentityRole(_uiState.value.requireSuccessData().account.uri, null),
        uploadMediaAttachment = uploadMediaAttachment,
        scope = viewModelScope,
    )

    private fun buildAttachmentFile(file: ContentProviderFile) = PostStatusFile(
        file = file,
        description = null,
        uploadJob = buildUploadFileJob(file),
    )

    fun onMediaDeleteClick(image: PostStatusFile) {
        val imageAttachment = _uiState.value
            .requireSuccessData()
            .attachment
            ?.asImageAttachmentOrNull ?: return
        _uiState.updateOnSuccess { state ->
            state.copy(
                attachment = PostStatusAttachment.ImageAttachment(imageAttachment.imageList.remove { it == image })
            )
        }
    }

    fun onCancelUploadClick(file: PostStatusFile) {
        file.uploadJob.cancel()
    }

    fun onRetryClick(file: PostStatusFile) {
        file.uploadJob.upload()
    }

    fun onDescriptionInputted(file: PostStatusFile, description: String) {
        _uiState.updateOnSuccess { state ->
            val imageList = state.attachment?.asImageAttachmentOrNull?.imageList ?: emptyList()
            val newImageList = imageList.map {
                if (it == file) {
                    it.copy(description = description)
                } else {
                    it
                }
            }
            state.copy(attachment = PostStatusAttachment.ImageAttachment(newImageList))
        }
        val mediaId = (file.uploadJob.uploadState.value as? UploadMediaJob.UploadState.Success)?.id
        if (mediaId.isNullOrEmpty().not()) {
            launchInViewModel {
                val role = IdentityRole(_uiState.value.requireSuccessData().account.uri, null)
                clientManager.getClient(role)
                    .mediaRepo
                    .updateMedia(
                        id = mediaId!!,
                        description = description,
                    )
            }
        }
    }

    fun onLanguageSelected(locale: Locale) {
        _uiState.updateOnSuccess { state ->
            state.copy(language = locale)
        }
    }

    fun onPollClicked() {
        if (_uiState.value.successDataOrNull()?.attachment is PostStatusAttachment.Poll) return
        _uiState.updateOnSuccess { state ->
            state.copy(
                attachment = PostStatusAttachment.Poll(
                    optionList = listOf("", ""),
                    multiple = false,
                    duration = 1.days,
                )
            )
        }
    }

    fun onPollContentChanged(index: Int, content: String) {
        _uiState.updateOnSuccess { state ->
            val pollAttachment = state.attachment!!.asPollAttachment
            state.copy(
                attachment = pollAttachment.copy(
                    optionList = pollAttachment.optionList.updateIndex(index) { content }
                )
            )
        }
    }

    fun onAddPollItemClick() {
        _uiState.updateOnSuccess { state ->
            val pollAttachment = state.attachment!!.asPollAttachment
            state.copy(
                attachment = pollAttachment.copy(
                    optionList = pollAttachment.optionList.plus("")
                )
            )
        }
    }

    fun onRemovePollItemClick(index: Int) {
        _uiState.updateOnSuccess { state ->
            val pollAttachment = state.attachment!!.asPollAttachment
            state.copy(
                attachment = pollAttachment.copy(
                    optionList = pollAttachment.optionList.removeIndex(index)
                )
            )
        }
    }

    fun onRemovePollClick() {
        _uiState.updateOnSuccess { state ->
            state.copy(attachment = null)
        }
    }

    fun onPollStyleSelect(multiple: Boolean) {
        _uiState.updateOnSuccess { state ->
            val poll = state.attachment!!.asPollAttachment
            state.copy(
                attachment = poll.copy(
                    multiple = multiple
                )
            )
        }
    }

    fun onWarningContentChanged(content: String) {
        _uiState.updateOnSuccess {
            it.copy(warningContent = content)
        }
    }

    fun onVisibilityChanged(visibility: PostStatusVisibility) {
        _uiState.updateOnSuccess {
            it.copy(visibility = visibility)
        }
    }

    fun onDurationSelect(duration: Duration) {
        _uiState.updateOnSuccess { state ->
            val pollAttachment = state.attachment!!.asPollAttachment
            state.copy(
                attachment = pollAttachment.copy(duration = duration)
            )
        }
    }

    fun onCustomEmojiPick(emoji: CustomEmoji) {
        onContentChanged(":${emoji.shortcode}:")
    }

    fun onEmojiDeleteClick() {
//        _uiState.updateOnSuccess { state ->
//            state.copy(
//                content = "",
//            )
//        }
    }

    fun onPostClick() {
        val currentUiState = _uiState.value.requireSuccessData()
        val account = currentUiState.account
        val attachment = currentUiState.attachment

        when (attachment) {
            is PostStatusAttachment.ImageAttachment -> {
                val allSuccess = attachment.imageList
                    .map { it.uploadJob.uploadState.value }
                    .all { it is UploadMediaJob.UploadState.Success }
                if (!allSuccess) return
            }

            is PostStatusAttachment.VideoAttachment -> {
                if (attachment.video.uploadJob.uploadState.value !is UploadMediaJob.UploadState.Success) return
            }

            is PostStatusAttachment.Poll -> {
                for (option in attachment.optionList) {
                    if (option.isEmpty()) return
                }
            }

            else -> {}
        }
        if (attachment != null) {
            attachment.asImageAttachmentOrNull?.imageList?.map { it.uploadJob }
        }
        launchInViewModel {
            _postState.emit(LoadableState.loading())
            postNoAttachmentStatus(
                account = account,
                content = currentUiState.content,
                attachment = attachment,
                sensitive = currentUiState.sensitive,
                replyToId = replyToId,
                spoilerText = currentUiState.warningContent,
                visibility = currentUiState.visibility,
                language = currentUiState.language,
            ).onSuccess {
                _postState.emit(LoadableState.success(Unit))
            }.onFailure {
                _postState.emit(LoadableState.failed(it))
            }
        }
    }
}
