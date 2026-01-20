package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.framework.utils.AspectRatio
import com.zhangke.framework.utils.ImageCompressUtils
import com.zhangke.framework.utils.KB
import com.zhangke.framework.utils.MB
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.VideoUtils
import com.zhangke.framework.utils.mapForErrorMessage
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import sh.christian.ozone.api.model.Blob

class UploadBlobUseCase(
    private val clientManager: BlueskyClientManager,
    private val platformUriHelper: PlatformUriHelper,
) {

    companion object {

        private val BSKY_BLOB_MAX_SIZE = 930.KB
    }

    suspend operator fun invoke(
        locator: PlatformLocator,
        fileUri: PlatformUri,
    ): Result<Pair<Blob, AspectRatio?>> {
        return runCatching {
            val file = platformUriHelper.read(fileUri)
                ?: throw RuntimeException("File invalid!")
            var bytes = file.readBytes()
                ?: throw RuntimeException("File invalid!")
            var aspect: AspectRatio? = null
            if (file.isVideo) {
                VideoUtils().getVideoAspect(fileUri.toString())?.let {
                    aspect = it
                }
            } else {
                val result = withContext(Dispatchers.IO) {
                    ImageCompressUtils().compress(bytes, BSKY_BLOB_MAX_SIZE)
                }
                bytes = result.bytes
                aspect = result.ratio
            }
            val blob = clientManager.getClient(locator).uploadBlobCatching(bytes).getOrThrow().blob
            blob to aspect
        }.mapForErrorMessage("Upload blob failed")
    }
}
