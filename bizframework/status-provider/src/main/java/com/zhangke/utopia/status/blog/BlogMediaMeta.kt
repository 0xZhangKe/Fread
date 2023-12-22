package com.zhangke.utopia.status.blog

import kotlinx.serialization.Serializable

@Serializable
sealed class BlogMediaMeta {

    @Serializable
    data class ImageMeta(
        val original: LayoutMeta?,
        val small: LayoutMeta?,
        val focus: FocusMeta?,
    ) : BlogMediaMeta() {

        @Serializable
        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
        )

        @Serializable
        data class FocusMeta(
            val x: Float?,
            val y: Float?,
        )
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
    ) : BlogMediaMeta() {

        @Serializable
        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
            val frameRate: String?,
            val duration: Double?,
            val bitrate: Int?,
        )
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
    ) : BlogMediaMeta() {

        @Serializable
        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
            val frameRate: String?,
            val duration: Double?,
            val bitrate: Int?,
        )
    }

    @Serializable
    data class AudioMeta(
        val length: String?,
        val duration: Double?,
        val audioEncode: String?,
        val audioBitrate: String?,
        val audioChannels: String?,
        val original: FrameMeta?,
    ) : BlogMediaMeta() {

        @Serializable
        data class FrameMeta(
            val duration: Double?,
            val bitrate: Int?,
        )
    }
}

fun BlogMediaMeta.asImageMeta(): BlogMediaMeta.ImageMeta = this as BlogMediaMeta.ImageMeta

fun BlogMediaMeta.asImageMetaOrNull(): BlogMediaMeta.ImageMeta? = this as? BlogMediaMeta.ImageMeta
