package com.zhangke.fread.activitypub.app.internal.usecase.emoji

import com.zhangke.fread.status.model.Emoji

class MapCustomEmojiUseCase () {

    operator fun invoke(content: String, emojis: List<Emoji>): String {
        return content
//        var mappedContent = content
//        emojis.forEach {
//            mappedContent =
//                content.replace(
//                    ":${it.shortcode}:",
//                    "<img src=\"${it.url}\" alt=\"${it.shortcode}\" />",
//                )
//        }
//        return mappedContent
    }
}