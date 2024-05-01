package com.zhangke.utopia.status.richtext

import com.zhangke.utopia.status.ui.richtext.composable.RichText
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import moe.tlaster.ktml.dom.Element
import moe.tlaster.ktml.dom.Node

fun buildRichText(
    document: String,
    mentions: List<Mention>,
    baseUrl: FormalBaseUrl?,
): com.zhangke.utopia.status.ui.richtext.composable.RichText {
    return com.zhangke.utopia.status.ui.richtext.composable.RichText(
        document = document,
        postProcess = { element ->
            if (baseUrl == null) {
                return@RichText element
            }
            replaceMentionAndHashtag(
                mentions = mentions,
                node = element,
                host = baseUrl.host,
            )
            element
        }
    )
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

fun Blog.preParseRichText() {
    humanizedContent.parseElement()
    humanizedSpoilerText.parseElement()
}

fun List<Status>.preParseRichText() {
    forEach { it.intrinsicBlog.preParseRichText() }
}
