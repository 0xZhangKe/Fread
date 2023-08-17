package com.zhangke.utopia.status.blog

sealed class BlogMediaMeta {

    data class ImageMeta(
        val original: LayoutMeta?,
        val small: LayoutMeta?,
        val focus: FocusMeta?,
    ) : BlogMediaMeta() {

        data class LayoutMeta(
            val width: Int?,
            val height: Int?,
            val size: String?,
            val aspect: Float?,
        )

        data class FocusMeta(
            val x: Float?,
            val y: Float?,
        )
    }

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

    data class AudioMeta(
        val length: String?,
        val duration: Double?,
        val audioEncode: String?,
        val audioBitrate: String?,
        val audioChannels: String?,
        val original: FrameMeta?,
    ) : BlogMediaMeta() {

        data class FrameMeta(
            val duration: Double?,
            val bitrate: Int?,
        )
    }
}

fun BlogMediaMeta.asImageMeta(): BlogMediaMeta.ImageMeta = this as BlogMediaMeta.ImageMeta

fun BlogMediaMeta.asImageMetaOrNull(): BlogMediaMeta.ImageMeta? = this as? BlogMediaMeta.ImageMeta

