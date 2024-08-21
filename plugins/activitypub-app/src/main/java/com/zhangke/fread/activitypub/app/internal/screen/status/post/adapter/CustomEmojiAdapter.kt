package com.zhangke.fread.activitypub.app.internal.screen.status.post.adapter

import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.GroupedCustomEmojiCell
import javax.inject.Inject

class CustomEmojiAdapter @Inject constructor() {

    fun toEmojiCell(
        customEmojiList: List<CustomEmoji>
    ): List<GroupedCustomEmojiCell> {
        return customEmojiList.filter { it.visibleInPicker }
            .groupBy { it.category }
            .entries
            .map { GroupedCustomEmojiCell(it.key, it.value) }
    }
}
