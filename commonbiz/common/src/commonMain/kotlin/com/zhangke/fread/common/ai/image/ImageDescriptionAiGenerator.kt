package com.zhangke.fread.common.ai.image

import kotlinx.coroutines.flow.Flow

class ImageDescriptionAiGenerator {

    fun startGenerate(
        imageUri: String,
    ): Flow<ImageDescriptionGenerateState> {
        return ImageDescriptionAiGeneratorImpl().startGenerate(imageUri)
    }

    fun startDownload(): Flow<ImageAiModelDownloadState> {
        return ImageDescriptionAiGeneratorImpl().startDownload()
    }
}

expect class ImageDescriptionAiGeneratorImpl() {

    fun startGenerate(
        imageUri: String,
    ): Flow<ImageDescriptionGenerateState>

    fun startDownload(): Flow<ImageAiModelDownloadState>
}

sealed interface ImageDescriptionGenerateState {

    data object Idle : ImageDescriptionGenerateState

    data object Downloadable : ImageDescriptionGenerateState

    data class Generating(val description: String) : ImageDescriptionGenerateState

    data object GenerateFinished : ImageDescriptionGenerateState

    data class Failure(val error: Throwable) : ImageDescriptionGenerateState

    data object Unavailable : ImageDescriptionGenerateState
}

sealed interface ImageAiModelDownloadState {

    data object Idle : ImageAiModelDownloadState

    data object Started : ImageAiModelDownloadState

    data class Downloading(val downloadedSize: Long) : ImageAiModelDownloadState

    data object Success : ImageAiModelDownloadState

    data class Failure(val error: Throwable) : ImageAiModelDownloadState
}
