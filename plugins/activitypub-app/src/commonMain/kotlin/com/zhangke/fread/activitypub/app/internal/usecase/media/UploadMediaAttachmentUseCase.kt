package com.zhangke.fread.activitypub.app.internal.usecase.media

import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UploadMediaAttachmentUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val platformUriHelper: PlatformUriHelper,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        fileUri: PlatformUri,
        onProgress: (Float) -> Unit,
    ): Result<String> {
        val client = clientManager.getClient(role)
        return try {
            val stream = platformUriHelper.read(fileUri)
                ?: return Result.failure(RuntimeException("File invalid!"))
            val bytes = stream.readBytes()
                ?: return Result.failure(RuntimeException("File invalid!"))
            client.mediaRepo.postFile(
                fileName = stream.fileName,
                fileSize = stream.size.bytes,
                byteArray = bytes,
                fileMediaType = stream.mimeType,
                onProgress = {
                    onProgress(it)
                },
            ).map { it.id }
        } catch (e: Throwable) {
            Result.failure(RuntimeException("File invalid!"))
        }
    }
}
