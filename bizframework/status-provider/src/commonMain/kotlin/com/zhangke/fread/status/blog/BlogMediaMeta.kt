package com.zhangke.fread.status.blog

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
sealed class BlogMediaMeta: PlatformSerializable {

    @Serializable
    data class ImageMeta(
        val original: LayoutMeta?,
        val small: LayoutMeta?,
        val focus: FocusMeta?,
    ) : BlogMediaMeta(), PlatformSerializable {

        @Serializable
        data class LayoutMeta(
            val width: Long?,
            val height: Long?,
            val size: String?,
            val aspect: Float?,
        ): PlatformSerializable

        @Serializable
        data class FocusMeta(
            val x: Float?,
            val y: Float?,
        ): PlatformSerializable
    }

    @Serializable
    data class VideoMeta(
        val length: String?,
        val duration: Double?,
        val fps: Int?,
        val size: String?,
        val width: Long?,
        val height: Long?,
        val aspect: Float?,
        val audioEncode: String?,
        val audioBitrate: String?,
        val audioChannels: String?,
        val original: LayoutMeta?,
        val small: LayoutMeta?,
    ) : BlogMediaMeta(), PlatformSerializable {

        @Serializable
        data class LayoutMeta(
            val width: Long?,
            val height: Long?,
            val size: String?,
            val aspect: Float?,
            val frameRate: String?,
            val duration: Double?,
            val bitrate: Int?,
        ): PlatformSerializable
    }

    @Serializable
    data class GifvMeta(
        val length: String?,
        val duration: Double?,
        val fps: Int?,
        val size: String?,
        val width: Int?,
        val height: Int?,
        val aspect: Float?,
        val original: LayoutMeta?,
        val small: LayoutMeta?,
    ) : BlogMediaMeta(), PlatformSerializable {

        @Serializable
        data class LayoutMeta(
            val width: Long?,
            val height: Long?,
            val size: String?,
            val aspect: Float?,
            val frameRate: String?,
            val duration: Double?,
            val bitrate: Int?,
        ): PlatformSerializable
    }

    @Serializable
    data class AudioMeta(
        val length: String?,
        val duration: Double?,
        val audioEncode: String?,
        val audioBitrate: String?,
        val audioChannels: String?,
        val original: FrameMeta?,
    ) : BlogMediaMeta(), PlatformSerializable {

        @Serializable
        data class FrameMeta(
            val duration: Double?,
            val bitrate: Int?,
        ): PlatformSerializable
    }
}

fun BlogMediaMeta.asImageMeta(): BlogMediaMeta.ImageMeta = this as BlogMediaMeta.ImageMeta

fun BlogMediaMeta.asImageMetaOrNull(): BlogMediaMeta.ImageMeta? = this as? BlogMediaMeta.ImageMeta
