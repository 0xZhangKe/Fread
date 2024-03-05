package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubCustomEmojiEntity
import com.zhangke.utopia.activitypub.app.internal.model.CustomEmoji
import com.zhangke.utopia.status.model.Emoji
import javax.inject.Inject

class ActivityPubCustomEmojiEntityAdapter @Inject constructor() {

    fun toCustomEmoji(entity: ActivityPubCustomEmojiEntity) = CustomEmoji(
        shortcode = entity.shortcode,
        url = entity.url,
        staticUrl = entity.staticUrl,
        visibleInPicker = entity.visibleInPicker,
        category = entity.category,
    )

    fun toEmoji(entity: ActivityPubCustomEmojiEntity): Emoji {
        return Emoji(
            shortcode = entity.shortcode,
            url = entity.url,
            staticUrl = entity.staticUrl,
        )
    }
}
