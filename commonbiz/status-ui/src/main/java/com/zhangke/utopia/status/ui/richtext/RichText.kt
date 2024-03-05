package com.zhangke.utopia.status.ui.richtext

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.Mention
import moe.tlaster.ktml.Ktml
import moe.tlaster.ktml.dom.Element
import moe.tlaster.ktml.dom.Node
import java.io.File

@Composable
fun EmojiFont() {
    val fontFamily = FontFamily(
        Font(File("")),
        IconFont()
    )
    Text(
        text = "",
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                emojiSupportMatch = EmojiSupportMatch.Default,
            )
        )
    )
}

class IconFont() : Font {

    override val loadingStrategy: FontLoadingStrategy
        get() = FontLoadingStrategy.Async

    override val style: FontStyle
        get() = FontStyle.Normal

    override val weight: FontWeight
        get() = FontWeight.Normal
}

private fun parseContent(
    host: String,
    text: String,
    emojis: List<Emoji>,
    mentions: List<Mention>,
): Element {
    var content = text
    emojis.forEach {
        content =
            content.replace(
                ":${it.shortcode}:",
                "<img src=\"${it.url}\" alt=\"${it.shortcode}\" />",
            )
    }
    val body = Ktml.parse(content)
    body.children.forEach {
        replaceMentionAndHashtag(mentions, it, host)
    }
    return body
}

private fun replaceMentionAndHashtag(
    mentions: List<Mention>,
    node: Node,
    host: String,
) {
    if (node is Element) {
        val href = node.attributes["href"]
        val mention = mentions.firstOrNull { it.url == href }
        if (mention != null) {
            val id = mention.id
//            if (id != null) {
//                node.attributes["href"] = AppDeepLink.Profile(userKey = MicroBlogKey(id, host))
//            }
        } else if (node.innerText.startsWith("#")) {
//            node.attributes["href"] = AppDeepLink.Search(node.innerText)
        }
        node.children.forEach { replaceMentionAndHashtag(mentions, it, host) }
    }
}
