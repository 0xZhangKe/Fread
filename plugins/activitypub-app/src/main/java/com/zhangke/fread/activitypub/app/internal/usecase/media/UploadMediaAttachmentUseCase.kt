package com.zhangke.fread.activitypub.app.internal.usecase.media

import android.net.Uri
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadMediaAttachmentUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        fileUri: Uri,
        onProgress: (Float) -> Unit,
    ): Result<String> {
        val contentFile = withContext(Dispatchers.IO) { fileUri.toContentProviderFile() }
            ?: return Result.failure(RuntimeException("File invalid!"))
        val client = clientManager.getClient(role)
        try {
            contentFile.openInputStream().use { inputStream ->
                if (inputStream == null) return Result.failure(RuntimeException("File invalid!"))
                return client.mediaRepo.postFile(
                    fileName = contentFile.fileName,
                    fileSize = contentFile.size.length,
                    byteArray = inputStream.readBytes(),
                    fileMediaType = contentFile.mimeType,
                    onProgress = {
                        onProgress(it)
                    },
                ).map { it.id }
            }
        } catch (e: Throwable) {
            return Result.failure(RuntimeException("File invalid!"))
        }
    }
}
