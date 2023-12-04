package com.zhangke.utopia.activitypub.app.internal.screen.status.post

import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.usecase.media.UploadMediaAttachmentUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UploadMediaJob(
    private val file: ContentProviderFile,
    private val account: ActivityPubLoggedAccount,
    private val uploadMediaAttachment: UploadMediaAttachmentUseCase,
    private val scope: CoroutineScope,
) {

    private var uploadingJob: Job? = null

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun upload() {
        if (_uploadState.value is UploadState.Uploading) return
        uploadingJob = scope.launch(Dispatchers.IO) {
            val uploadState = UploadState.Uploading()
            _uploadState.value = uploadState
            val result = uploadMediaAttachment(
                account = account,
                fileUri = file.uri,
                description = null,
                onProgress = {
                    scope.launch {
                        uploadState.onProgressChanged(it)
                    }
                },
            )
            if (result.isFailure) {
                _uploadState.value = UploadState.Failed(result.exceptionOrNull())
            } else {
                _uploadState.value = UploadState.Success(result.getOrThrow())
            }
        }
    }

    fun cancel() {
        uploadingJob?.cancel()
        _uploadState.value = UploadState.Failed(CancelByManualException())
    }

    sealed interface UploadState {

        data object Idle : UploadState

        class Uploading : UploadState {

            private val _progress = MutableSharedFlow<Float>()
            val progress: SharedFlow<Float> get() = _progress

            suspend fun onProgressChanged(currentProgress: Float) {
                _progress.emit(currentProgress)
            }
        }

        class Failed(val reason: Throwable?) : UploadState

        data class Success(val id: String) : UploadState
    }

    class CancelByManualException : RuntimeException()
}
