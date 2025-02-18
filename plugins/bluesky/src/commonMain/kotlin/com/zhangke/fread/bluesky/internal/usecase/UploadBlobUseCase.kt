package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.framework.utils.ImageCompressUtils
import com.zhangke.framework.utils.MB
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.model.Blob

class UploadBlobUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val platformUriHelper: PlatformUriHelper,
) {

    companion object {

        private val BSKY_BLOB_MAX_SIZE = 1.MB
    }

    suspend operator fun invoke(
        role: IdentityRole,
        fileUri: PlatformUri,
    ): Result<Blob> {
        return runCatching {
            val file = platformUriHelper.read(fileUri)
                ?: throw RuntimeException("File invalid!")
            var bytes = file.readBytes()
                ?: throw RuntimeException("File invalid!")
            if (!file.isVideo) {
                bytes = ImageCompressUtils().compress(bytes, BSKY_BLOB_MAX_SIZE)
            }
            clientManager.getClient(role).uploadBlobCatching(bytes).getOrThrow().blob
        }
    }
}
