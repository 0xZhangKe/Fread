package com.zhangke.utopia.activitypub.app

import android.net.Uri
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.utopia.activitypub.app.internal.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubMediaAttachmentEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.status.GetUserStatusDataSourceFromUriUseCase
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.Status
import com.zhangke.utopia.status.status.UploadAttachmentMediaResult
import com.zhangke.utopia.status.uri.StatusProviderUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val getUserStatusFromUri: GetUserStatusDataSourceFromUriUseCase,
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val mediaEntityAdapter: ActivityPubMediaAttachmentEntityAdapter,
) : IStatusResolver {

    override suspend fun uploadMediaAttachment(
        account: LoggedAccount,
        fileUri: Uri,
        description: String?,
        onProgress: (Float) -> Unit,
    ): Result<UploadAttachmentMediaResult>? {
        if (account !is ActivityPubLoggedAccount) return null
        val contentFile = withContext(Dispatchers.IO) { fileUri.toContentProviderFile() } ?: return Result.failure(RuntimeException("File invalid!"))
        val client = obtainActivityPubClient(account.webFinger.host)
        try {
            contentFile.openInputStream().use { inputStream ->
                if (inputStream == null) return Result.failure(RuntimeException("File invalid!"))
                return client.mediaRepo.postFile(
                    fileName = contentFile.fileName,
                    fileSize = contentFile.size.length,
                    inputStream = inputStream,
                    fileMediaType = contentFile.mimeType,
                    onProgress = onProgress,
                ).map { mediaEntityAdapter.toActivityPubMediaAttachmentEntity(it) }
            }
        } catch (e: Throwable) {
            return Result.failure(RuntimeException("File invalid!"))
        }
    }

    override fun getStatusDataSourceByUri(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        return getUserStatusFromUri(uri)
    }

    override fun postStatus() {
        TODO("Not yet implemented")
    }
}
