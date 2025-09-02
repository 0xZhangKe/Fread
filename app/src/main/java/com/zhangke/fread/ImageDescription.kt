package com.zhangke.fread

import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.common.ai.image.ImageDescriptionGenerateState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ImageDescription {

    fun startGenerate(
        imageUri: String,
    ): Flow<ImageDescriptionGenerateState> {
        return flow {
            val options = ImageDescriberOptions.builder(appContext).build()
            val imageDescriber = ImageDescription.getClient(options)
            val featureStatusResult = imageDescriber.checkFeatureStatus().await()
            if (featureStatusResult.isFailure) {
                emit(ImageDescriptionGenerateState.Failure(featureStatusResult.exceptionOrNull()!!))
                return@flow
            }
            val featureStatus = featureStatusResult.getOrThrow()
            if (featureStatus == FeatureStatus.UNAVAILABLE){
                emit(ImageDescriptionGenerateState.Unavailable)
                return@flow
            }
            if (featureStatus == FeatureStatus.DOWNLOADABLE){

            }
        }
    }

    private suspend fun ListenableFuture<Int>.await(): Result<Int> {
        return suspendCancellableCoroutine { continuation ->
            Futures.addCallback<Int>(
                this,
                object : FutureCallback<Int> {
                    override fun onSuccess(featureStatus: Int) {
                        continuation.resume(Result.success(featureStatus.toInt()))
                    }

                    override fun onFailure(t: Throwable) {
                        continuation.resume(Result.failure(t))
                    }
                },
                ContextCompat.getMainExecutor(appContext),
            )
        }
    }
}
