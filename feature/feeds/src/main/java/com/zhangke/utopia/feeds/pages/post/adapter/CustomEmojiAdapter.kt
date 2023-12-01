package com.zhangke.utopia.feeds.pages.post.adapter

import com.zhangke.utopia.status.emoji.CustomEmoji
import com.zhangke.utopia.status.ui.emoji.CustomEmojiCell
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
