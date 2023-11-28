package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.collections.remove
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.FileUtils
import com.zhangke.framework.utils.StorageSize
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PostStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadableState.loading<PostStatusUiState>())
    val uiState: StateFlow<LoadableState<PostStatusUiState>> = _uiState.asStateFlow()

    init {
        launchInViewModel {
            val accountManager = statusProvider.accountManager
            val loggedAccount = accountManager.getLoggedAccount()
            val allLoggedAccount =
                statusProvider.accountManager.getAllLoggedAccount().getOrNull() ?: emptyList()
            if (loggedAccount == null) {
                _uiState.updateToFailed(IllegalStateException("Not login!"))
            } else {
                _uiState.value = LoadableState.success(
                    PostStatusUiState(
                        account = loggedAccount,
                        availableAccountList = allLoggedAccount,
                        content = "",
                        attachment = null,
                        maxMediaCount = 4,
                        sensitive = false,
                        language = Locale.ROOT,
                    )
                )
            }
        }
    }

    fun onContentChanged(inputtedText: String) {
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
            ?.asImageAttachment
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
        account = _uiState.value.requireSuccessData().account,
        statusResolver = statusProvider.statusResolver,
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
            ?.asImageAttachment ?: return
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
            val imageList = state.attachment?.asImageAttachment?.imageList ?: emptyList()
            val newImageList = imageList.map {
                if (it == file) {
                    it.copy(description = description)
                } else {
                    it
                }
            }
            state.copy(attachment = PostStatusAttachment.ImageAttachment(newImageList))
        }
    }

    fun onLanguageSelected(locale: Locale) {
        _uiState.updateOnSuccess { state ->
            state.copy(language = locale)
        }
    }

    fun onPostClick() {
    }
}
