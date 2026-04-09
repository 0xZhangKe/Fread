package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.utils.mapForErrorMessage
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.common.utils.StorageHelper
import com.zhangke.fread.status.model.PlatformLocator
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import okio.FileSystem
import okio.SYSTEM
import sh.christian.ozone.api.model.Blob

class UploadImageByImageUrlUseCase(
    private val clientManager: BlueskyClientManager,
    private val storageHelper: StorageHelper,
) {

    suspend operator fun invoke(locator: PlatformLocator, imageUrl: String): Result<Blob> {
        return runCatching {
            val fileSystem = FileSystem.SYSTEM
            val cacheDir = storageHelper.cacheDir.resolve("bluesky_remote_images")
            fileSystem.createDirectories(cacheDir)
            val localPath = cacheDir.resolve("remote-image-${imageUrl.hashCode()}.tmp")
            try {
                val bytes = sharedHttpClient.get {
                    url { takeFrom(imageUrl) }
                }.body<ByteArray>()
                fileSystem.write(localPath) { write(bytes) }
                val localBytes = fileSystem.read(localPath) {
                    readByteArray()
                }
                clientManager.getClient(locator).uploadBlobCatching(localBytes).getOrThrow().blob
            } finally {
                runCatching {
                    if (fileSystem.exists(localPath)) {
                        fileSystem.delete(localPath)
                    }
                }
            }
        }.mapForErrorMessage("Upload image by url failed")
    }
}
