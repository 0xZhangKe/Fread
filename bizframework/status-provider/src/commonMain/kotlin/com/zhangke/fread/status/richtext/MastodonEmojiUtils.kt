package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.model.Emoji

object MastodonEmojiUtils {

    private val EMOJI_CODE_PATTERN = (":(\\w+):").toRegex()

    fun visiteEmojis(
        text: String,
        emojis: Map<String, Emoji>,
        onPlainTextFound: (String) -> Unit,
        onEmojiFound: (Emoji) -> Unit,
    ) {
        if (text.isEmpty()) return
        if (emojis.isEmpty()) {
            onPlainTextFound(text)
            return
        }
        val results = EMOJI_CODE_PATTERN.findAll(text)

        var index = 0
        results.iterator().forEach {
            if (it.range.first > index) {
                onPlainTextFound(text.substring(index, it.range.first))
            }
            val emojiCode = it.groups[1]?.value
            if (emojiCode != null) {
                val emoji = emojis[emojiCode]
                if (emoji != null) {
                    onEmojiFound(emoji)
                } else {
                    onPlainTextFound(it.value)
                }
            } else {
                onPlainTextFound(it.value)
            }
            index = it.range.last + 1
        }
        if (index < text.length) {
            onPlainTextFound(text.substring(index))
        }
    }
}
