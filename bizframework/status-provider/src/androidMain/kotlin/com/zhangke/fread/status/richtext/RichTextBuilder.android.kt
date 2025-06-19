package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.model.Mention
import moe.tlaster.ktml.dom.Element
import moe.tlaster.ktml.dom.Node

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
    return "fread://${host}/user/${mention.id}"
}
