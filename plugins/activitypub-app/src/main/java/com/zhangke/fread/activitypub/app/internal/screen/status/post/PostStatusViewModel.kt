package com.zhangke.fread.activitypub.app.internal.screen.status.post

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.collections.remove
import com.zhangke.framework.collections.removeIndex
import com.zhangke.framework.collections.updateIndex
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase.GenerateInitPostStatusUiStateUseCase
import com.zhangke.fread.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.emoji.GetCustomEmojiUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.media.UploadMediaAttachmentUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.platform.GetInstancePostStatusRulesUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.PostStatusUseCase
import com.zhangke.fread.common.utils.MentionTextUtil
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@HiltViewModel(assistedFactory = PostStatusViewModel.Factory::class)
class PostStatusViewModel @AssistedInject constructor(
    private val getCustomEmoji: GetCustomEmojiUseCase,
    private val getInstancePostStatusRules: GetInstancePostStatusRulesUseCase,
    private val generateInitPostStatusUiState: GenerateInitPostStatusUiStateUseCase,
    private val uploadMediaAttachment: UploadMediaAttachmentUseCase,
    private val clientManager: ActivityPubClientManager,
    private val postStatus: PostStatusUseCase,
    private val platformUriTransformer: PlatformUriTransformer,
    @Assisted private val screenParams: PostStatusScreenParams,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(screenParams: PostStatusScreenParams): PostStatusViewModel
    }

    private val _uiState = MutableStateFlow(LoadableState.loading<PostStatusUiState>())
    val uiState: StateFlow<LoadableState<PostStatusUiState>> = _uiState.asStateFlow()

    private val _postState = MutableSharedFlow<LoadableState<Unit>>()
    val postState: SharedFlow<LoadableState<Unit>> = _postState.asSharedFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> get() = _snackMessage

    private var searchMentionUserJob: Job? = null

    init {
        launchInViewModel {
            generateInitPostStatusUiState(screenParams)
                .onSuccess {
                    _uiState.value = LoadableState.success(it)
                }.onFailure {
                    _uiState.updateToFailed(it)
                }
        }
        loadPostStatusRules()
    }

    private fun loadPostStatusRules() {
        launchInViewModel {
            _uiState.mapNotNull { it.successDataOrNull()?.account?.platform }
                .distinctUntilChanged()
                .mapNotNull { FormalUri.from(it.uri) }
                .mapNotNull { platformUriTransformer.parse(it) }
                .collect { platformInsights ->
                    val platformBaseUrl = platformInsights.serverBaseUrl
                    getCustomEmoji(platformBaseUrl)
                        .onSuccess {
                            _uiState.updateOnSuccess { state -> state.copy(emojiList = it) }
                        }.onFailure {
                            _snackMessage.emitTextMessageFromThrowable(it)
                        }
                    getInstancePostStatusRules(platformBaseUrl)
                        .onSuccess {
                            _uiState.updateOnSuccess { state -> state.copy(rules = it) }
                        }.onFailure {
                            _snackMessage.emitTextMessageFromThrowable(it)
                        }
                }
        }
    }

    fun onSwitchAccountClick(account: ActivityPubLoggedAccount) {
        _uiState.updateOnSuccess {
            it.copy(account = account)
        }
    }

    fun onContentChanged(inputtedText: TextFieldValue) {
        if (_uiState.value.requireSuccessData().allowedInputCount <= 0) return
        _uiState.updateOnSuccess {
            it.copy(content = inputtedText.text)
        }
        maybeSearchAccountForMention(inputtedText)
    }

    private fun maybeSearchAccountForMention(content: TextFieldValue) {
        val contentText = content.text
        if (contentText == _uiState.value.successDataOrNull()?.initialContent) return
        val account = _uiState.value.successDataOrNull()?.account ?: return
        searchMentionUserJob?.cancel()
        val role = IdentityRole(accountUri = account.uri, null)
        val mentionText = MentionTextUtil.findTypingMentionName(content)?.removePrefix("@")
        if (mentionText == null || mentionText.length < 2) {
            _uiState.updateOnSuccess {
                it.copy(mentionState = LoadableState.idle())
            }
            return
        }
        searchMentionUserJob = launchInViewModel {
            _uiState.updateOnSuccess {
                it.copy(mentionState = LoadableState.loading())
            }
            clientManager.getClient(role = role)
                .accountRepo
                .search(
                    query = mentionText,
                    limit = 10,
                    resolve = false,
                )
                .onSuccess { list ->
                    _uiState.updateOnSuccess {
                        it.copy(mentionState = LoadableState.success(list))
                    }
                }.onFailure {
                    _uiState.updateOnSuccess {
                        it.copy(mentionState = LoadableState.idle())
                    }
                }
        }
        searchMentionUserJob?.invokeOnCancel {
            _uiState.updateOnSuccess {
                it.copy(mentionState = LoadableState.idle())
            }
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
        val attachmentFile = buildLocalAttachmentFile(file)
        attachmentFile.uploadJob.upload()
        _uiState.updateOnSuccess {
            it.copy(attachment = PostStatusAttachment.Video(attachmentFile))
        }
    }

    private fun onAddImageList(uriList: List<ContentProviderFile>) {
        val imageList = mutableListOf<PostStatusMediaAttachmentFile>()
        _uiState.value
            .requireSuccessData()
            .attachment
            ?.asImageOrNull
            ?.imageList
            ?.let { imageList += it }
        uriList.forEach { uri ->
            val attachmentFile = buildLocalAttachmentFile(uri)
            attachmentFile.uploadJob.upload()
            imageList += attachmentFile
        }
        _uiState.updateOnSuccess {
            it.copy(attachment = PostStatusAttachment.Image(imageList))
        }
    }

    private fun buildUploadFileJob(file: ContentProviderFile) = UploadMediaJob(
        file = file,
        role = IdentityRole(_uiState.value.requireSuccessData().account.uri, null),
        uploadMediaAttachment = uploadMediaAttachment,
        scope = viewModelScope,
    )

    private fun buildLocalAttachmentFile(
        file: ContentProviderFile,
    ) = PostStatusMediaAttachmentFile.LocalFile(
        file = file,
        description = null,
        uploadJob = buildUploadFileJob(file),
    )

    fun onMediaDeleteClick(image: PostStatusMediaAttachmentFile) {
        val attachment = _uiState.value
            .requireSuccessData()
            .attachment ?: return
        val imageAttachment = attachment.asImageOrNull
        if (imageAttachment != null) {
            _uiState.updateOnSuccess { state ->
                state.copy(
                    attachment = PostStatusAttachment.Image(imageAttachment.imageList.remove { it == image })
                )
            }
            return
        }
        val videoAttachment = attachment.asVideoOrNull
        if (videoAttachment != null) {
            _uiState.updateOnSuccess { state ->
                state.copy(attachment = null)
            }
        }
    }

    fun onCancelUploadClick(file: PostStatusMediaAttachmentFile.LocalFile) {
        file.uploadJob.cancel()
    }

    fun onRetryClick(file: PostStatusMediaAttachmentFile.LocalFile) {
        file.uploadJob.upload()
    }

    fun onDescriptionInputted(file: PostStatusMediaAttachmentFile, description: String) {
        _uiState.updateOnSuccess { state ->
            val imageList = state.attachment?.asImageOrNull?.imageList ?: emptyList()
            val newImageList = imageList.map {
                if (it == file) {
                    when (it) {
                        is PostStatusMediaAttachmentFile.LocalFile -> it.copy(description = description)
                        is PostStatusMediaAttachmentFile.RemoteFile -> it.copy(description = description)
                    }
                } else {
                    it
                }
            }
            state.copy(attachment = PostStatusAttachment.Image(newImageList))
        }
        if (file is PostStatusMediaAttachmentFile.LocalFile) {
            val mediaId = file.fileId
            if (!mediaId.isNullOrEmpty()) {
                launchInViewModel {
                    val role = IdentityRole(_uiState.value.requireSuccessData().account.uri, null)
                    clientManager.getClient(role)
                        .mediaRepo
                        .updateMedia(
                            id = mediaId,
                            description = description,
                        ).onFailure {
                            _snackMessage.emitTextMessageFromThrowable(it)
                        }
                }
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

    fun onVisibilityChanged(visibility: StatusVisibility) {
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

    fun onPostClick() {
        val currentUiState = _uiState.value.requireSuccessData()
        val account = currentUiState.account
        val attachment = currentUiState.attachment
        if (currentUiState.content.isEmpty() && currentUiState.attachment == null) {
            _snackMessage.emitInViewModel(textOf(R.string.post_status_content_is_empty))
            return
        }
        when (attachment) {
            is PostStatusAttachment.Image -> {
                val allSuccess = attachment.imageList
                    .mapNotNull { it as? PostStatusMediaAttachmentFile.LocalFile }
                    .map { it.uploadJob.uploadState.value }
                    .all { it is UploadMediaJob.UploadState.Success }
                if (!allSuccess) {
                    _snackMessage.emitInViewModel(textOf(R.string.post_status_media_is_not_upload))
                    return
                }
            }

            is PostStatusAttachment.Video -> {
                val uploadJob = attachment.video
                    .let { it as? PostStatusMediaAttachmentFile.LocalFile }
                    ?.uploadJob
                if (uploadJob != null && uploadJob.uploadState.value !is UploadMediaJob.UploadState.Success) {
                    _snackMessage.emitInViewModel(textOf(R.string.post_status_media_is_not_upload))
                    return
                }
            }

            is PostStatusAttachment.Poll -> {
                if (currentUiState.content.isEmpty()) {
                    _snackMessage.emitInViewModel(textOf(R.string.post_status_content_is_empty))
                    return
                }
                for (option in attachment.optionList) {
                    if (option.isEmpty()) {
                        _snackMessage.emitInViewModel(textOf(R.string.post_status_poll_is_empty))
                        return
                    }
                }
            }

            else -> {}
        }
        launchInViewModel {
            _postState.emit(LoadableState.loading())
            postStatus(
                account = account,
                content = currentUiState.content,
                attachment = attachment,
                originStatusId = (screenParams as? PostStatusScreenParams.EditStatusParams)?.blog?.id,
                sensitive = currentUiState.sensitive,
                replyToId = (screenParams as? PostStatusScreenParams.ReplyStatusParams)?.replyToBlogId,
                spoilerText = currentUiState.warningContent,
                visibility = currentUiState.visibility,
                language = currentUiState.language,
            ).onSuccess {
                _postState.emit(LoadableState.success(Unit))
            }.onFailure {
                _postState.emit(LoadableState.idle())
                val errorMessage = textOf(
                    R.string.post_status_failed,
                    it.localizedMessage.ifNullOrEmpty { "unknown error" },
                )
                _snackMessage.emit(errorMessage)
            }
        }
    }
}
