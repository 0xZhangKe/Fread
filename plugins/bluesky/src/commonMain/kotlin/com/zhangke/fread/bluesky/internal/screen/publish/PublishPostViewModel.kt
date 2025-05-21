package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.graph.ListView
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.GetAllListsUseCase
import com.zhangke.fread.bluesky.internal.usecase.PublishingPostUseCase
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.ReplySetting
import com.zhangke.fread.status.model.StatusList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class PublishPostViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getAllLists: GetAllListsUseCase,
    private val platformUriHelper: PlatformUriHelper,
    private val configManager: FreadConfigManager,
    private val publishingPost: PublishingPostUseCase,
    @Assisted private val role: IdentityRole,
    @Assisted replyBlogJsonString: String?,
    @Assisted quoteBlogJsonString: String?,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
            replyBlogJsonString: String?,
            quoteBlogJsonString: String?,
        ): PublishPostViewModel
    }

    private val _uiState = MutableStateFlow(PublishPostUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    private var publishJob: Job? = null

    init {
        launchInViewModel(Dispatchers.IO) {
            val reply: Blog? = replyBlogJsonString?.let {
                globalJson.fromJson<Blog>(it)
            }
            val quote: Blog? = quoteBlogJsonString?.let {
                globalJson.fromJson<Blog>(it)
            }
            _uiState.update { it.copy(replyBlog = reply, quoteBlog = quote) }
        }
        launchInViewModel {
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            _uiState.update { it.copy(account = account) }
        }
        launchInViewModel {
            configManager.getBskyPublishLanguage().let {
                _uiState.update { state ->
                    val selectedLanguages = it.ifEmpty {
                        listOf("en")
                    }
                    state.copy(selectedLanguages = selectedLanguages)
                }
            }
        }
        loadUserList()
    }

    fun onContentChanged(text: TextFieldValue) {
        _uiState.update { it.copy(content = text) }
    }

    fun onQuoteChange(allowQuote: Boolean) {
        _uiState.update {
            it.copy(interactionSetting = it.interactionSetting.copy(allowQuote = allowQuote))
        }
    }

    fun onReplySettingChange(replySetting: ReplySetting) {
        _uiState.update {
            it.copy(
                interactionSetting = it.interactionSetting.copy(replySetting = replySetting)
            )
        }
    }

    fun onSettingOptionsSelected(option: ReplySetting.CombineOption) {
        _uiState.update { state ->
            val options = state.interactionSetting.replySetting.let { it as? ReplySetting.Combined }
                ?.options?.toMutableList() ?: mutableListOf()
            if (option in options) {
                options.remove(option)
            } else {
                options.add(option)
            }
            state.copy(
                interactionSetting = state.interactionSetting.copy(
                    replySetting = ReplySetting.Combined(options),
                )
            )
        }
    }

    fun onMediaSelected(medias: List<PlatformUri>) {
        if (medias.isEmpty()) return
        viewModelScope.launch {
            val currentAttachment = uiState.value.attachment
            val fileList = medias.map {
                async { platformUriHelper.read(it) }
            }.awaitAll().filterNotNull()
            val attachment = if (fileList.first().isVideo) {
                PublishPostMediaAttachment.Video(fileList.first().toUiFile(true))
            } else {
                PublishPostMediaAttachment.Image(fileList.map { it.toUiFile(false) })
            }
            _uiState.update { it.copy(attachment = currentAttachment.merge(attachment)) }
        }
    }

    private fun PublishPostMediaAttachment?.merge(
        attachment: PublishPostMediaAttachment
    ): PublishPostMediaAttachment {
        if (this == null) return attachment
        if (this is PublishPostMediaAttachment.Video || attachment is PublishPostMediaAttachment.Video) {
            return attachment
        }
        val files = (this as PublishPostMediaAttachment.Image).files
        return PublishPostMediaAttachment.Image(files + (attachment as PublishPostMediaAttachment.Image).files)
    }

    private fun ContentProviderFile.toUiFile(isVideo: Boolean): PublishPostMediaAttachmentFile {
        return PublishPostMediaAttachmentFile(
            file = this,
            alt = null,
            isVideo = isVideo,
        )
    }

    fun onMediaAltChanged(file: PublishPostMedia, alt: String) {
        file as PublishPostMediaAttachmentFile
        val attachment = uiState.value.attachment
        if (attachment is PublishPostMediaAttachment.Video) {
            _uiState.update {
                it.copy(attachment = PublishPostMediaAttachment.Video(file.copy(alt = alt)))
            }
        } else {
            val files = (attachment as PublishPostMediaAttachment.Image).files.map {
                if (it == file) {
                    it.copy(alt = alt)
                } else {
                    it
                }
            }
            _uiState.update { it.copy(attachment = PublishPostMediaAttachment.Image(files)) }
        }
    }

    fun onMediaDeleteClick(file: PublishPostMedia) {
        val attachment = uiState.value.attachment
        if (attachment is PublishPostMediaAttachment.Video) {
            _uiState.update { it.copy(attachment = null) }
        } else {
            val files = (attachment as PublishPostMediaAttachment.Image).files.filter { it != file }
            _uiState.update { it.copy(attachment = PublishPostMediaAttachment.Image(files)) }
        }
    }

    fun onLanguageSelected(selectedLanguages: List<String>) {
        launchInViewModel {
            configManager.updateBskyPublishLanguage(selectedLanguages)
        }
        _uiState.update { it.copy(selectedLanguages = selectedLanguages) }
    }

    fun onPublishClick() {
        if (publishJob?.isActive == true) return
        publishJob?.cancel()
        val account = uiState.value.account ?: return
        publishJob = launchInViewModel {
            _uiState.update { it.copy(publishing = true) }
            publishingPost(
                account = account,
                content = uiState.value.content.text,
                selectedLanguages = uiState.value.selectedLanguages,
                interactionSetting = uiState.value.interactionSetting,
                replyBlog = uiState.value.replyBlog,
                quoteBlog = uiState.value.quoteBlog,
                attachment = uiState.value.attachment,
            ).onFailure { t ->
                _uiState.update { it.copy(publishing = false) }
                _snackBarMessageFlow.emitTextMessageFromThrowable(t)
            }.onSuccess {
                _uiState.update { it.copy(publishing = false) }
                _finishPageFlow.emit(Unit)
            }
        }
    }

    private fun loadUserList() {
        launchInViewModel {
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            getAllLists(role, Did(account.did))
                .map { lists -> lists.map { listView -> listView.toStatusList() } }
                .onSuccess { lists -> _uiState.update { it.copy(list = lists) } }
        }
    }

    private fun ListView.toStatusList(): StatusList {
        return StatusList(
            name = this.name,
            uri = this.uri.atUri,
            cid = this.cid.cid,
        )
    }
}
