package com.zhangke.fread.common.ai.image

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class ImageDescriptionAiGeneratorImpl {

    actual fun startGenerate(
        imageUri: String,
    ): Flow<ImageDescriptionGenerateState> {
        return flow {
            emit(ImageDescriptionGenerateState.Failure(UnsupportedOperationException("Image description generation is not supported on iOS.")))
        }
    }

    actual fun startDownload(): Flow<ImageAiModelDownloadState>{
        return flow {
            emit(ImageAiModelDownloadState.Failure(UnsupportedOperationException("Image description model download is not supported on iOS.")))
        }
    }
}
