package com.zhangke.utopia.activitypub.app.internal.usecase.media

import android.net.Uri
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.utopia.activitypub.app.internal.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadMediaAttachmentUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        account: ActivityPubLoggedAccount,
        fileUri: Uri,
        description: String?,
        onProgress: (Float) -> Unit,
    ): Result<String> {
        val contentFile = withContext(Dispatchers.IO) { fileUri.toContentProviderFile() } ?: return Result.failure(RuntimeException("File invalid!"))
        val client = clientManager.getClient(account.webFinger.host.toBaseUrl())
        try {
            contentFile.openInputStream().use { inputStream ->
                if (inputStream == null) return Result.failure(RuntimeException("File invalid!"))
                return client.mediaRepo.postFile(
                    fileName = contentFile.fileName,
                    fileSize = contentFile.size.length,
                    inputStream = inputStream,
                    description = description,
                    fileMediaType = contentFile.mimeType,
                    onProgress = onProgress,
                ).map { it.id }
            }
        } catch (e: Throwable) {
            return Result.failure(RuntimeException("File invalid!"))
        }
    }
}
