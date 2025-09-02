package com.zhangke.fread.commonbiz.shared.screen.image

import com.zhangke.fread.common.ai.image.ImageAiModelDownloadState
import com.zhangke.fread.common.ai.image.ImageDescriptionGenerateState

data class GenerateImageAltUiState(
    val imageUri: String,
    val generatedText: String,
    val generatingState: ImageDescriptionGenerateState,
    val downloadState: ImageAiModelDownloadState,
) {

    val generateEnable: Boolean
        get() = generatingState is ImageDescriptionGenerateState.Idle ||
                generatingState is ImageDescriptionGenerateState.Downloadable ||
                generatingState is ImageDescriptionGenerateState.Failure ||
                generatingState is ImageDescriptionGenerateState.GenerateFinished ||
                downloadState is ImageAiModelDownloadState.Downloading

    companion object {

        fun default(
            imageUri: String,
        ) = GenerateImageAltUiState(
            imageUri = imageUri,
            generatedText = "",
            generatingState = ImageDescriptionGenerateState.Idle,
            downloadState = ImageAiModelDownloadState.Idle,
        )
    }
}
