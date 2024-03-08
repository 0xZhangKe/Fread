package com.zhangke.utopia.status.ui.richtext

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.Mention
import moe.tlaster.ktml.dom.Element
import moe.tlaster.ktml.dom.Node

@Composable
fun UtopiaRichText(
    modifier: Modifier,
    document: String,
    host: String,
    emojis: List<Emoji>,
    mentions: List<Mention>,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    RichText(
        modifier = modifier,
        document = document,
        layoutDirection = layoutDirection,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        textStyle = textStyle,
    )
}

//private fun parseContent(
//    host: String,
//    text: String,
//    emojis: List<Emoji>,
//    mentions: List<Mention>,
//): Element {
//    var content = text
//    emojis.forEach {
//        content =
//            content.replace(
//                ":${it.shortcode}:",
//                "<img src=\"${it.url}\" alt=\"${it.shortcode}\" />",
//            )
//    }
//    val body = Ktml.parse(content)
//    body.children.forEach {
//        replaceMentionAndHashtag(mentions, it, host)
//    }
//    return body
//}

private fun replaceMentionAndHashtag(
    mentions: List<Mention>,
    node: Node,
    host: String,
) {
    if (node is Element) {
        val href = node.attributes["href"]
        val mention = mentions.firstOrNull { it.url == href }
        if (mention != null) {
            node.attributes["href"] = buildMentionUrl(mention, host)
        }
        node.children.forEach { replaceMentionAndHashtag(mentions, it, host) }
    }
}

private fun buildMentionUrl(
    mention: Mention,
    host: String,
): String {
    return "utopia://${host}/user/${mention.id}"
}
