package com.zhangke.utopia.activitypub.app.internal.screen.status.post.adapter

import com.zhangke.utopia.activitypub.app.internal.model.CustomEmoji
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.composable.CustomEmojiCell
import javax.inject.Inject

class CustomEmojiAdapter @Inject constructor() {

    fun toEmojiCell(
        rowCount: Int,
        customEmojiList: List<CustomEmoji>
    ): List<CustomEmojiCell> {
        return customEmojiList.groupBy { it.category }
            .flatMap {
                val list = mutableListOf<CustomEmojiCell>()
                list += CustomEmojiCell.Title(it.key)
                list.addAll(it.value.chunked(rowCount).map(CustomEmojiCell::EmojiLine))
                list
            }
    }
}
