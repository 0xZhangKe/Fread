package com.zhangke.framework.utils

import android.media.MediaMetadataRetriever
import androidx.core.net.toUri

actual class VideoUtils {

    actual fun getVideoAspect(uri: String): AspectRatio? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(appContext, uri.toUri())
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            ?.toIntOrNull()
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            ?.toIntOrNull()
        retriever.release()
        if (width == null || height == null) return null
        return AspectRatio(
            width.toLong(),
            height.toLong()
        )
    }
}
