package com.zhangke.fread.commonbiz.shared.screen.image

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.ai.image.ImageAiModelDownloadState
import com.zhangke.fread.common.ai.image.ImageDescriptionAiGenerator
import com.zhangke.fread.common.ai.image.ImageDescriptionGenerateState
import com.zhangke.fread.common.di.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class GenerateImageAltViewModel @Inject constructor(
    @Assisted private val imageUri: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
        fun create(imageUri: String): GenerateImageAltViewModel
    }

    private val _uiState = MutableStateFlow(GenerateImageAltUiState.default(imageUri))
    val uiState = _uiState

    private var downloadJob: Job? = null

    fun onGenerateClick() {
        if (!_uiState.value.generateEnable) return
        launchInViewModel {
            ImageDescriptionAiGenerator().startGenerate(imageUri)
                .collect { state ->
                    _uiState.update {
                        val wholeText = buildString {
                            append(it.generatedText)
                            if (state is ImageDescriptionGenerateState.Generating) {
                                append(state.description)
                            }
                        }
                        it.copy(
                            generatingState = state,
                            generatedText = wholeText,
                        )
                    }
                }
        }
    }

    fun onDoNotDownloadClick() {
        _uiState.update { it.copy(generatingState = ImageDescriptionGenerateState.Idle) }
    }

    fun onGenerateFailedClick() {
        _uiState.update { it.copy(generatingState = ImageDescriptionGenerateState.Idle) }
    }

    fun onDownloadClick() {
        _uiState.update { it.copy(generatingState = ImageDescriptionGenerateState.Idle) }
        val downloadState = _uiState.value.downloadState
        if (downloadState !is ImageAiModelDownloadState.Idle &&
            downloadState !is ImageAiModelDownloadState.Failure
        ) {
            return
        }
        if (downloadJob?.isActive == true) return
        downloadJob = launchInViewModel {
            ImageDescriptionAiGenerator().startDownload()
                .collect { state ->
                    _uiState.update { it.copy(downloadState = state) }
                }
        }
    }

    fun onDownloadCancelClick() {
        downloadJob?.cancel()
        _uiState.update { it.copy(downloadState = ImageAiModelDownloadState.Idle) }
    }

    fun onDownloadSuccessClick() {
        _uiState.update {
            it.copy(
                generatingState = ImageDescriptionGenerateState.Idle,
                downloadState = ImageAiModelDownloadState.Idle,
            )
        }
    }

    fun onDownloadFailureClick() {
        _uiState.update {
            it.copy(
                generatingState = ImageDescriptionGenerateState.Idle,
                downloadState = ImageAiModelDownloadState.Idle,
            )
        }
    }
}
