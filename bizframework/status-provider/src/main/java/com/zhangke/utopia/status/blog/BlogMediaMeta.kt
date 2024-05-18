package com.zhangke.utopia.status.blog

import kotlinx.serialization.Serializable

@Serializable
sealed class BlogMediaMeta: java.io.Serializable {

    @Serializable
    data class ImageMeta(
        val original: LayoutMeta?,
        val small: LayoutMeta?,
        val focus: FocusMeta?,
    ) : BlogMediaMeta(), java.io.Serializable {

        @Serializable
        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
        ): java.io.Serializable

        @Serializable
        data class FocusMeta(
            val x: Float?,
            val y: Float?,
        ): java.io.Serializable
    }

    @Serializable
    data class VideoMeta(
        val length: String?,
        val duration: Double?,
        val fps: Int?,
        val size: String?,
        val width: Int?,
        val height: Int?,
        val aspect: Float?,
        val audioEncode: String?,
        val audioBitrate: String?,
        val audioChannels: String?,
        val original: LayoutMeta?,
        val small: LayoutMeta?,
    ) : BlogMediaMeta(), java.io.Serializable {

        @Serializable
        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
            val frameRate: String?,
            val duration: Double?,
            val bitrate: Int?,
        ): java.io.Serializable
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
    ) : BlogMediaMeta(), java.io.Serializable {

        @Serializable
        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
            val frameRate: String?,
            val duration: Double?,
            val bitrate: Int?,
        ): java.io.Serializable
    }

    @Serializable
    data class AudioMeta(
        val length: String?,
        val duration: Double?,
        val audioEncode: String?,
        val audioBitrate: String?,
        val audioChannels: String?,
        val original: FrameMeta?,
    ) : BlogMediaMeta(), java.io.Serializable {

        @Serializable
        data class FrameMeta(
            val duration: Double?,
            val bitrate: Int?,
        ): java.io.Serializable
    }
}

fun BlogMediaMeta.asImageMeta(): BlogMediaMeta.ImageMeta = this as BlogMediaMeta.ImageMeta

fun BlogMediaMeta.asImageMetaOrNull(): BlogMediaMeta.ImageMeta? = this as? BlogMediaMeta.ImageMeta
