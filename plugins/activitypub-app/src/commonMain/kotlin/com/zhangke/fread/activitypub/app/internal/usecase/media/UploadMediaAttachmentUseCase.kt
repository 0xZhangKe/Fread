package com.zhangke.fread.activitypub.app.internal.usecase.media

import com.zhangke.activitypub.api.MediaRepo
import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.activitypub.entities.ActivityPubResponse
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.IdentityRole
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.seconds

class UploadMediaAttachmentUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        file: ContentProviderFile,
        onProgress: (Float) -> Unit = {},
    ): Result<String> {
        val mediaRepo = clientManager.getClient(role).mediaRepo
        val response = mediaRepo.uploadFile(file, onProgress)
        if (response.isFailure) return Result.failure(response.exceptionOrThrow())
        val (code, attachment) = response.getOrThrow()
        if (code != HttpStatusCode.Accepted.value) return Result.success(attachment.id)
        var repeatCount = 0
        val interval = 3.seconds
        while (repeatCount < 10) {
            val mediaResponse = mediaRepo.getMedia(attachment.id)
            if (mediaResponse.isFailure || mediaResponse.getOrNull()?.code == HttpStatusCode.PartialContent.value) {
                delay(interval)
            } else {
                return mediaResponse.map { it.response.id }
            }
            repeatCount++
        }
        return Result.failure(RuntimeException("File upload failed, processing timeout!"))
    }

    private suspend fun MediaRepo.uploadFile(
        file: ContentProviderFile,
        onProgress: (Float) -> Unit = {},
    ): Result<ActivityPubResponse<ActivityPubMediaAttachmentEntity>> {
        return try {
            val bytes = file.readBytes()
                ?: return Result.failure(RuntimeException("File invalid!"))
            this.postFile(
                fileName = file.fileName,
                fileSize = file.size.bytes,
                byteArray = bytes,
                fileMediaType = file.mimeType,
                onProgress = onProgress,
            )
        } catch (t: Throwable) {
            Result.failure(RuntimeException("File invalid!"))
        }
    }
}
