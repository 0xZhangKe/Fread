package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubMediaAttachmentEntity
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.status.UploadAttachmentMediaResult
import javax.inject.Inject

class ActivityPubMediaAttachmentEntityAdapter @Inject constructor() {

    fun toActivityPubMediaAttachmentEntity(entity: ActivityPubMediaAttachmentEntity): UploadAttachmentMediaResult {
        return UploadAttachmentMediaResult(
            id = entity.id,
            url = entity.url,
            mediaType = convertMediaType(entity.type),
            description = entity.description,
            blurhash = entity.blurhash,
        )
    }

    private fun convertMediaType(mediaType: String): BlogMediaType {
        return when (mediaType) {
            ActivityPubMediaAttachmentEntity.TYPE_IMAGE -> BlogMediaType.IMAGE
            ActivityPubMediaAttachmentEntity.TYPE_AUDIO -> BlogMediaType.AUDIO
            ActivityPubMediaAttachmentEntity.TYPE_VIDEO -> BlogMediaType.VIDEO
            ActivityPubMediaAttachmentEntity.TYPE_GIFV -> BlogMediaType.GIFV
            else -> BlogMediaType.UNKNOWN
        }
    }
}