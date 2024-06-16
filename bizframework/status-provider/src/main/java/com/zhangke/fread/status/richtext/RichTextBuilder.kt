package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.status.model.Status
import moe.tlaster.ktml.dom.Element
import moe.tlaster.ktml.dom.Node

fun buildRichText(
    document: String,
    mentions: List<Mention> = emptyList(),
    hashTags: List<HashtagInStatus> = emptyList(),
    emojis: List<Emoji> = emptyList(),
): RichText {
    return RichText(
        document = document,
        mentions = mentions,
        hashTags = hashTags,
        emojis = emojis,
    )
//    return RichText(
//        document = document,
//        postProcess = { element ->
//            if (baseUrl == null) {
//                return@RichText element
//            }
//            replaceMentionAndHashtag(
//                mentions = mentions,
//                node = element,
//                host = baseUrl.host,
//            )
//            element
//        }
//    )
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
    return "fread://${host}/user/${mention.id}"
}

fun Blog.preParseRichText() {
    author.humanizedName.parse()
    humanizedContent.parse()
    humanizedSpoilerText.parse()
    humanizedDescription.parse()
}

fun Status.preParseRichText(){
    triggerAuthor.humanizedName.parse()
    intrinsicBlog.preParseRichText()
}

fun List<Status>.preParseRichText() {
    forEach {
        it.preParseRichText()
    }
}
