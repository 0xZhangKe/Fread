package com.zhangke.fread.activitypub.app.internal.screen.status.post.adapter

import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.CustomEmojiCell
import javax.inject.Inject

class CustomEmojiAdapter @Inject constructor() {

    fun toEmojiCell(
        rowCount: Int,
        customEmojiList: List<CustomEmoji>
    ): List<CustomEmojiCell> {
        return customEmojiList
            .filter { it.visibleInPicker }
            .groupBy { it.category }
            .flatMap {
                val list = mutableListOf<CustomEmojiCell>()
                list += CustomEmojiCell.Title(it.key)
                list.addAll(it.value.chunked(rowCount).map(CustomEmojiCell::EmojiLine))
                list
            }
    }
}
