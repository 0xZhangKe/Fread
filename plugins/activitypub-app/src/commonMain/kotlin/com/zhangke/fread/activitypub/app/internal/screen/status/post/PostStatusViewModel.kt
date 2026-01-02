package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.activitypub.entities.ActivityPubPreferencesEntity
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
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.initLocale
import com.zhangke.fread.activitypub.app.internal.adapter.toQuoteApprovalPolicy
import com.zhangke.fread.activitypub.app.internal.adapter.toStatusVisibility
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase.GenerateInitPostStatusUiStateUseCase
import com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase.PublishPostUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.emoji.GetCustomEmojiUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.platform.GetInstancePostStatusRulesUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.utils.MentionTextUtil
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.QuoteApprovalPolicy
import com.zhangke.fread.status.model.StatusVisibility
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class PostStatusViewModel @Inject constructor(
    private val getCustomEmoji: GetCustomEmojiUseCase,
    private val getInstancePostStatusRules: GetInstancePostStatusRulesUseCase,
    private val generateInitPostStatusUiState: GenerateInitPostStatusUiStateUseCase,
    private val clientManager: ActivityPubClientManager,
    private val publishPost: PublishPostUseCase,
    @Assisted private val screenParams: PostStatusScreenParams,
    private val platformUriHelper: PlatformUriHelper,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(screenParams: PostStatusScreenParams): PostStatusViewModel
    }

    private val _uiState = MutableStateFlow(LoadableState.loading<PostStatusUiState>())
    val uiState: StateFlow<LoadableState<PostStatusUiState>> = _uiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> get() = _snackMessage

    private val _publishSuccessFlow = MutableSharedFlow<Unit>()
    val publishSuccessFlow: SharedFlow<Unit> get() = _publishSuccessFlow

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
        loadPostingSettings()
    }

    private fun loadPostingSettings() {
        launchInViewModel {
            _uiState.mapNotNull { it.successDataOrNull()?.account }
                .distinctUntilChanged()
                .mapNotNull { it.locator }
                .collect { locator ->
                    getCustomEmoji(locator)
                        .onSuccess {
                            _uiState.updateOnSuccess { state -> state.copy(emojiList = it) }
                        }.onFailure {
                            _snackMessage.emitTextMessageFromThrowable(it)
                        }
                    getInstancePostStatusRules(locator)
                        .onSuccess {
                            _uiState.updateOnSuccess { state -> state.copy(rules = it) }
                        }.onFailure {
                            _snackMessage.emitTextMessageFromThrowable(it)
                        }
                    if (screenParams !is PostStatusScreenParams.EditStatusParams) {
                        clientManager.getClient(locator)
                            .accountRepo
                            .getPreferences()
                            .onSuccess { preferences ->
                                _uiState.updateOnSuccess { state ->
                                    fillDefaultSetting(state, preferences)
                                }
                            }
                    }
                }
        }
    }

    private fun fillDefaultSetting(
        state: PostStatusUiState,
        preferences: ActivityPubPreferencesEntity,
    ): PostStatusUiState {
        val keepInitVisibility = state.replyToBlog != null || state.isQuotingBlogMode
        return state.copy(
            visibility = if (keepInitVisibility) {
                state.visibility
            } else {
                preferences.postingDefaultVisibility.toStatusVisibility()
            },
            quoteApprovalPolicy = preferences.postingDefaultQuotePolicy?.toQuoteApprovalPolicy()
                ?: state.quoteApprovalPolicy,
            language = preferences.postingDefaultLanguage?.let { initLocale(it) }
                ?: state.language,
        )
    }

    fun onSwitchAccountClick(account: LoggedAccount) {
        _uiState.updateOnSuccess {
            it.copy(account = account as ActivityPubLoggedAccount)
        }
    }

    fun onContentChanged(inputtedText: TextFieldValue) {
        _uiState.updateOnSuccess {
            it.copy(content = inputtedText)
        }
        maybeSearchAccountForMention(inputtedText)
    }

    private fun maybeSearchAccountForMention(content: TextFieldValue) {
        val account = _uiState.value.successDataOrNull()?.account ?: return
        searchMentionUserJob?.cancel()
        val platformLocator =
            PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
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
            clientManager.getClient(locator = platformLocator)
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

    fun onMediaSelected(list: List<PlatformUri>) {
        if (list.isEmpty()) return
        viewModelScope.launch {
            val fileList = list.map {
                async { platformUriHelper.read(it) }
            }.awaitAll().filterNotNull()
            val videoFile = fileList.firstOrNull { it.isVideo }
            if (videoFile != null) {
                onAddVideo(videoFile)
            } else {
                onAddImageList(fileList)
            }
        }
    }

    private fun onAddVideo(file: ContentProviderFile) {
        val attachmentFile = buildLocalAttachmentFile(file)
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
            imageList += buildLocalAttachmentFile(uri)
        }
        _uiState.updateOnSuccess {
            it.copy(attachment = PostStatusAttachment.Image(imageList))
        }
    }

    private fun buildLocalAttachmentFile(
        file: ContentProviderFile,
    ) = PostStatusMediaAttachmentFile.LocalFile(
        file = file,
        alt = null,
    )

    fun onMediaDeleteClick(image: PublishPostMedia) {
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

    fun onQuoteApprovalPolicySelect(policy: QuoteApprovalPolicy) {
        _uiState.updateOnSuccess { it.copy(quoteApprovalPolicy = policy) }
    }

    fun onDescriptionInputted(file: PublishPostMedia, description: String) {
        _uiState.updateOnSuccess { state ->
            val imageList = state.attachment?.asImageOrNull?.imageList ?: emptyList()
            val newImageList = imageList.map {
                if (it == file) {
                    when (it) {
                        is PostStatusMediaAttachmentFile.LocalFile -> it.copy(alt = description)
                        is PostStatusMediaAttachmentFile.RemoteFile -> it.copy(alt = description)
                    }
                } else {
                    it
                }
            }
            state.copy(attachment = PostStatusAttachment.Image(newImageList))
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

    fun onWarningContentChanged(content: TextFieldValue) {
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
        if (currentUiState.content.text.isEmpty() && currentUiState.attachment == null) {
            _snackMessage.emitInViewModel(textOf(LocalizedString.postStatusContentIsEmpty))
            return
        }
        if (attachment is PostStatusAttachment.Poll) {
            if (currentUiState.content.text.isEmpty()) {
                _snackMessage.emitInViewModel(textOf(LocalizedString.postStatusContentIsEmpty))
                return
            }
            for (option in attachment.optionList) {
                if (option.isEmpty()) {
                    _snackMessage.emitInViewModel(textOf(LocalizedString.post_status_poll_is_empty))
                    return
                }
            }
        }
        launchInViewModel {
            _uiState.updateOnSuccess { it.copy(publishing = true) }
            publishPost(
                account = account,
                uiState = currentUiState,
                editingBlogId = (screenParams as? PostStatusScreenParams.EditStatusParams)?.blog?.id,
            ).onSuccess {
                _uiState.updateOnSuccess { it.copy(publishing = false) }
                _publishSuccessFlow.emit(Unit)
            }.onFailure { t ->
                _uiState.updateOnSuccess { it.copy(publishing = false) }
                val errorMessage = textOf(
                    LocalizedString.postStatusFailed,
                    t.message.ifNullOrEmpty { "unknown error" }.take(180),
                )
                _snackMessage.emit(errorMessage)
            }
        }
    }
}
