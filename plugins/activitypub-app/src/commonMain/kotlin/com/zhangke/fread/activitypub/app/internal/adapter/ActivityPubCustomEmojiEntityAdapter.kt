package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubCustomEmojiEntity
import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import com.zhangke.fread.status.model.Emoji
import me.tatarka.inject.annotations.Inject

class ActivityPubCustomEmojiEntityAdapter @Inject constructor() {

    fun toCustomEmoji(entity: ActivityPubCustomEmojiEntity) = CustomEmoji(
        shortcode = entity.shortcode,
        url = entity.url,
        staticUrl = entity.staticUrl,
        visibleInPicker = entity.visibleInPicker,
        category = entity.category ?: "Default",
    )

    fun toEmoji(entity: ActivityPubCustomEmojiEntity): Emoji {
        return Emoji(
            shortcode = entity.shortcode,
            url = entity.url,
            staticUrl = entity.staticUrl,
        )
    }
}
