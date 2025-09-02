package com.zhangke.fread.common.ai.image

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.common.StreamingCallback
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.google.mlkit.genai.imagedescription.ImageDescriptionRequest
import com.google.mlkit.genai.imagedescription.ImageDescriptionResult
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

actual class ImageDescriptionAiGeneratorImpl {

    actual fun startGenerate(
        imageUri: String,
    ): Flow<ImageDescriptionGenerateState> {
        return callbackFlow {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                send(ImageDescriptionGenerateState.Failure(RuntimeException("Image description requires Android Pie or higher.")))
                close()
                return@callbackFlow
            }
            val imageDescriber =
                ImageDescription.getClient(ImageDescriberOptions.builder(appContext).build())
            val featureStatusResult = imageDescriber.checkFeatureStatus().await()
            if (featureStatusResult.isFailure) {
                send(ImageDescriptionGenerateState.Failure(featureStatusResult.exceptionOrNull()!!))
                close()
                return@callbackFlow
            }
            val featureStatus = featureStatusResult.getOrThrow()
            if (featureStatus == FeatureStatus.UNAVAILABLE) {
                send(ImageDescriptionGenerateState.Unavailable)
                close()
                return@callbackFlow
            }
            if (featureStatus == FeatureStatus.DOWNLOADABLE) {
                send(ImageDescriptionGenerateState.Downloadable)
                close()
                return@callbackFlow
            }
            if (featureStatus == FeatureStatus.DOWNLOADING) {
                send(ImageDescriptionGenerateState.Failure(RuntimeException("Model is downloading, please wait.")))
                close()
                return@callbackFlow
            }
            try {
                val bitmap = loadBitmap(imageUri)
                val imageDescriptionRequest = ImageDescriptionRequest.builder(bitmap).build()
                val callback = StreamingCallback { text ->
                    trySend(ImageDescriptionGenerateState.Generating(text))
                }
                val inferenceFuture = imageDescriber.runInference(imageDescriptionRequest, callback)
                Futures.addCallback<ImageDescriptionResult>(
                    inferenceFuture,
                    object : FutureCallback<ImageDescriptionResult> {
                        override fun onSuccess(result: ImageDescriptionResult?) {
                            trySend(ImageDescriptionGenerateState.GenerateFinished)
                            close()
                        }

                        override fun onFailure(t: Throwable) {
                            trySend(ImageDescriptionGenerateState.Failure(t))
                            close()
                        }
                    },
                    ContextCompat.getMainExecutor(appContext),
                )
            } catch (t: Throwable) {
                send(ImageDescriptionGenerateState.Failure(t))
                close()
            }
        }
    }

    actual fun startDownload(): Flow<ImageAiModelDownloadState> {
        return callbackFlow {
            val downloadCallback = object : DownloadCallback {
                override fun onDownloadCompleted() {
                    close()
                }

                override fun onDownloadFailed(p0: GenAiException) {
                    trySend(ImageAiModelDownloadState.Failure(p0))
                    close(p0)
                }

                override fun onDownloadProgress(p0: Long) {
                    trySend(ImageAiModelDownloadState.Downloading(p0))
                }

                override fun onDownloadStarted(p0: Long) {
                    trySend(ImageAiModelDownloadState.Started)
                }
            }
            val imageDescriber =
                ImageDescription.getClient(ImageDescriberOptions.builder(appContext).build())
            val featureStatus = imageDescriber.downloadFeature(downloadCallback)
            Futures.addCallback<Void>(
                featureStatus,
                object : FutureCallback<Void> {
                    override fun onSuccess(result: Void?) {}

                    override fun onFailure(t: Throwable) {
                        trySend(ImageAiModelDownloadState.Failure(t))
                        close(t)
                    }
                },
                ContextCompat.getMainExecutor(appContext),
            )
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

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun loadBitmap(imageUri: String): Bitmap {
        return withContext(Dispatchers.IO) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    appContext.contentResolver,
                    imageUri.toUri(),
                )
            )
        }
    }
}
