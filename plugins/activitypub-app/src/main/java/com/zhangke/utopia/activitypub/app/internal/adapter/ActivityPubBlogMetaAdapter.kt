package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubMediaMetaEntity
import com.zhangke.utopia.status.blog.BlogMediaMeta
import com.zhangke.utopia.status.blog.BlogMediaType
import javax.inject.Inject

class ActivityPubBlogMetaAdapter @Inject constructor() {

    fun adapt(
        type: BlogMediaType,
        entity: ActivityPubMediaMetaEntity,
    ): BlogMediaMeta? {
        return when (type) {
            BlogMediaType.IMAGE -> entity.toImageMeta()
            BlogMediaType.GIFV -> entity.toGifvMeta()
            BlogMediaType.VIDEO -> entity.toVideoMeta()
            BlogMediaType.AUDIO -> entity.toAudioMeta()
            else -> null
        }
    }

    private fun ActivityPubMediaMetaEntity.toImageMeta() = BlogMediaMeta.ImageMeta(
        original = original?.toImageLayoutMeta(),
        small = original?.toImageLayoutMeta(),
        focus = focus?.toImageFocusMeta(),
    )

    private fun ActivityPubMediaMetaEntity.LayoutMeta.toImageLayoutMeta() =
        BlogMediaMeta.ImageMeta.LayoutMeta(
            width = width,
            height = height,
            size = size,
            aspect = aspect,
        )

    private fun ActivityPubMediaMetaEntity.FocusMeta.toImageFocusMeta() =
        BlogMediaMeta.ImageMeta.FocusMeta(
            x = x,
            y = y,
        )

    private fun ActivityPubMediaMetaEntity.toVideoMeta() = BlogMediaMeta.VideoMeta(
        length = length,
        duration = duration,
        fps = fps,
        size = size,
        width = width,
        height = height,
        aspect = aspect,
        audioEncode = audioEncode,
        audioBitrate = audioBitrate,
        audioChannels = audioChannels,
        original = original?.toVideoLayoutMeta(),
        small = small?.toVideoLayoutMeta(),
    )

    private fun ActivityPubMediaMetaEntity.LayoutMeta.toVideoLayoutMeta() =
        BlogMediaMeta.VideoMeta.LayoutMeta(
            width = width,
            height = height,
            size = size,
            frameRate = frameRate,
            duration = duration,
            aspect = aspect,
            bitrate = bitrate,
        )

    private fun ActivityPubMediaMetaEntity.toGifvMeta() = BlogMediaMeta.GifvMeta(
        length = length,
        duration = duration,
        fps = fps,
        size = size,
        width = width,
        height = height,
        aspect = aspect,
        original = original?.toGifvLayoutMeta(),
        small = small?.toGifvLayoutMeta(),
    )

    private fun ActivityPubMediaMetaEntity.LayoutMeta.toGifvLayoutMeta() =
        BlogMediaMeta.GifvMeta.LayoutMeta(
            width = width,
            height = height,
            size = size,
            frameRate = frameRate,
            duration = duration,
            aspect = aspect,
            bitrate = bitrate,
        )

    private fun ActivityPubMediaMetaEntity.toAudioMeta() = BlogMediaMeta.AudioMeta(
        length = length,
        duration = duration,
        audioEncode = audioEncode,
        audioBitrate = audioBitrate,
        audioChannels = audioChannels,
        original = original?.toAudioLayoutMeta(),
    )

    private fun ActivityPubMediaMetaEntity.LayoutMeta.toAudioLayoutMeta() =
        BlogMediaMeta.AudioMeta.FrameMeta(
            duration = duration,
            bitrate = bitrate,
        )
}
