package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubCustomEmojiEntity
import com.zhangke.utopia.activitypub.app.internal.model.CustomEmoji
import javax.inject.Inject

class ActivityPubCustomEmojiEntityAdapter @Inject constructor() {

    fun toCustomEmoji(entity: ActivityPubCustomEmojiEntity) = CustomEmoji(
        shortcode = entity.shortcode,
        url = entity.url,
        staticUrl = entity.staticUrl,
        visibleInPicker = entity.visibleInPicker,
        category = entity.category,
    )
}
