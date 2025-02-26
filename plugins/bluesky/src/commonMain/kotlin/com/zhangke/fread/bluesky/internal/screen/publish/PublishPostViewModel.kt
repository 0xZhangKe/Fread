package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.embed.AspectRatio
import app.bsky.embed.Images
import app.bsky.embed.ImagesImage
import app.bsky.embed.Video
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.bluesky.internal.usecase.GetAllListsUseCase
import com.zhangke.fread.bluesky.internal.usecase.UploadBlobUseCase
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.model.JsonContent

class PublishPostViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getAllLists: GetAllListsUseCase,
    private val platformUriHelper: PlatformUriHelper,
    private val configManager: FreadConfigManager,
    private val uploadBlob: UploadBlobUseCase,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
        ): PublishPostViewModel
    }

    private val _uiState = MutableStateFlow(PublishPostUiState.default())
    val uiState = _uiState.asStateFlow()

    private var publishJob: Job? = null

    init {
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
                PublishPostMediaAttachment.Video(fileList.first().toUiFile())
            } else {
                PublishPostMediaAttachment.Image(fileList.map { it.toUiFile() })
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

    private fun ContentProviderFile.toUiFile(): PublishPostMediaAttachmentFile {
        return PublishPostMediaAttachmentFile(
            file = this,
            alt = null
        )
    }

    fun onMediaAltChanged(file: PublishPostMediaAttachmentFile, alt: String) {
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

    fun onMediaDeleteClick(file: PublishPostMediaAttachmentFile) {
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
        publishJob = launchInViewModel {
            _uiState.update { it.copy(publishing = true) }
            val client = clientManager.getClient(role)
            val embeds: Result<JsonContent>? =
                when (val attachment = uiState.value.attachment) {
                    is PublishPostMediaAttachment.Video -> {
                        uploadVideo(attachment.file)
                    }

                    is PublishPostMediaAttachment.Image -> {
                        uploadImages(attachment)
                    }

                    else -> null
                }

            client.applyWrites()
        }
    }

    private suspend fun uploadVideo(file: PublishPostMediaAttachmentFile): Result<JsonContent> {
        return uploadBlob(role = role, fileUri = file.file.uri)
            .map {
                Video(
                    video = it.first,
                    alt = file.alt,
                    aspectRatio = it.second?.convert(),
                ).bskyJson()
            }
    }

    private suspend fun uploadImages(image: PublishPostMediaAttachment.Image): Result<JsonContent> {
        val resultList: List<Result<ImagesImage>> = supervisorScope {
            image.files.map { file ->
                async {
                    uploadBlob(role = role, fileUri = file.file.uri).map {
                        ImagesImage(
                            image = it.first,
                            aspectRatio = it.second?.convert(),
                            alt = file.alt.orEmpty(),
                        )
                    }
                }
            }.awaitAll()
        }
        if (resultList.any { it.isFailure }) {
            return Result.failure(resultList.first { it.isFailure }.exceptionOrNull()!!)
        }
        return Result.success(Images(resultList.map { it.getOrThrow() }).bskyJson())
    }

    private fun com.zhangke.framework.utils.AspectRatio.convert(): AspectRatio {
        return AspectRatio(
            width = width,
            height = height,
        )
    }

    private fun loadUserList() {
        launchInViewModel {
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            getAllLists(role, Did(account.did))
                .onSuccess { lists -> _uiState.update { it.copy(list = lists) } }
        }
    }
}
