package com.zhangke.utopia.activitypub.app.internal.usecase.emoji

import com.zhangke.utopia.status.model.Emoji
import javax.inject.Inject

class MapCustomEmojiUseCase @Inject constructor() {

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
