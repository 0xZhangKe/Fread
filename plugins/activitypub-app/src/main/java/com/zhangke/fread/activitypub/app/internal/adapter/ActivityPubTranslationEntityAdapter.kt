package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubTranslationEntity
import com.zhangke.fread.status.blog.BlogTranslation
import javax.inject.Inject

class ActivityPubTranslationEntityAdapter @Inject constructor() {

    fun toTranslation(entity: ActivityPubTranslationEntity): BlogTranslation {
        return BlogTranslation(
            content = entity.content,
            spoilerText = entity.spoilerText,
            poll = entity.poll?.toPol(),
            attachment = entity.mediaAttachments?.toAttachment(),
            detectedSourceLanguage = entity.detectedSourceLanguage,
            provider = entity.provider,
        )
    }

    private fun ActivityPubTranslationEntity.Poll.toPol(): BlogTranslation.Poll {
        return BlogTranslation.Poll(
            id = this.id,
            options = this.options.map {
                BlogTranslation.Poll.Option(it.title)
            },
        )
    }

    private fun ActivityPubTranslationEntity.Attachment.toAttachment(): BlogTranslation.Attachment {
        return BlogTranslation.Attachment(
            id = this.id,
            description = this.description,
        )
    }
}
