package com.zhangke.fread.activitypub.app.internal.usecase.media

import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UploadMediaAttachmentUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        file: ContentProviderFile,
        onProgress: (Float) -> Unit = {},
    ): Result<String> {
        val client = clientManager.getClient(role)
        return try {
            val bytes = file.readBytes()
                ?: return Result.failure(RuntimeException("File invalid!"))
            client.mediaRepo.postFile(
                fileName = file.fileName,
                fileSize = file.size.bytes,
                byteArray = bytes,
                fileMediaType = file.mimeType,
                onProgress = onProgress,
            ).map { it.id }
        } catch (_: Throwable) {
            Result.failure(RuntimeException("File invalid!"))
        }
    }
}
