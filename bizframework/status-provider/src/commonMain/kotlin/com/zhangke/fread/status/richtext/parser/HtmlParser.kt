package com.zhangke.fread.status.richtext.parser

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import com.fleeksoft.ksoup.select.NodeVisitor
import com.zhangke.framework.architect.theme.primaryLight
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.OnLinkTargetClick
import com.zhangke.fread.status.richtext.model.RichLinkTarget

object HtmlParser {

    fun parse(
        document: String,
        emojis: List<Emoji>,
        mentions: List<Mention>,
        hashTags: List<HashtagInStatus>,
        onLinkTargetClick: OnLinkTargetClick,
        parsePossibleHashtag: Boolean = false,
    ): AnnotatedString {
        return buildAnnotatedString {
            Ksoup.parseBodyFragment(document)
                .body()
                .traverse(
                    ParseVisitor(
                        spanBuilder = this,
                        emojis = emojis.associateBy { it.shortcode },
                        mentions = mentions,
                        hashTags = hashTags.map { it.copy(name = it.name.lowercase()) },
                        onLinkTargetClick = onLinkTargetClick,
                        parsePossibleHashtag = parsePossibleHashtag,
                    )
                )
        }
    }

    private class ParseVisitor(
        private val spanBuilder: AnnotatedString.Builder,
        private val emojis: Map<String, Emoji>,
        private val mentions: List<Mention>,
        private val hashTags: List<HashtagInStatus>,
        private val parsePossibleHashtag: Boolean,
        private val onLinkTargetClick: OnLinkTargetClick,
    ) : NodeVisitor {

        private val popQueue = ArrayDeque<Int>()

        private var skip = false

        override fun head(node: Node, depth: Int) {
            if (skip) {
                return
            }
            if (node is TextNode) {
                spanBuilder.appendWithEmoji(node.text(), emojis)
                return
            }
            if (node is Element) {
                when (node.tagName()) {
                    "br" -> {
                        spanBuilder.append("\n")
                    }

                    "a" -> {
                        val href = node.attr("href")
                        var linkTarget: RichLinkTarget? = null
                        if (node.hasClass("hashtag")) {
                            val text = node.text()
                            if (text.startsWith("#")) {
                                if (parsePossibleHashtag) {
                                    linkTarget =
                                        RichLinkTarget.MaybeHashtagTarget(text.substring(1))
                                } else {
                                    val hashtagText = text.substring(1).lowercase()
                                    val hashTag = hashTags.firstOrNull { it.name == hashtagText }
                                    if (hashTag != null) {
                                        linkTarget = RichLinkTarget.HashtagTarget(hashTag)
                                    }
                                }
                            } else {
                                if (href.isNotEmpty()) {
                                    linkTarget = RichLinkTarget.UrlTarget(href)
                                }
                            }
                        } else if (node.hasClass("mention")) {
                            val id = mentions.firstOrNull { it.url == href }?.id
                            if (id != null) {
                                val mention = mentions.firstOrNull { it.id == id }
                                if (mention != null) {
                                    linkTarget = RichLinkTarget.MentionTarget(mention)
                                }
                            } else {
                                if (href.isNotEmpty()) {
                                    linkTarget = RichLinkTarget.UrlTarget(href)
                                }
                            }
                        } else if (href.isNotEmpty()) {
                            linkTarget = RichLinkTarget.UrlTarget(href)
                        }
                        if (linkTarget != null) {
                            popQueue.addLast(
                                spanBuilder.pushLink(
                                    LinkAnnotation.Clickable(
                                        tag = when (linkTarget) {
                                            is RichLinkTarget.UrlTarget -> linkTarget.url
                                            is RichLinkTarget.MentionTarget -> linkTarget.mention.id
                                            is RichLinkTarget.MentionDidTarget -> linkTarget.did
                                            is RichLinkTarget.HashtagTarget -> linkTarget.hashtag.name
                                            is RichLinkTarget.MaybeHashtagTarget -> linkTarget.hashtag
                                        },
                                        styles = TextLinkStyles(
                                            style = SpanStyle(color = primaryLight),
                                            hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                                        ),
                                        linkInteractionListener = {
                                            onLinkTargetClick(linkTarget)
                                        },
                                    )
                                )
                            )
                        } else {
                            // no href
                        }
                    }

                    "span" -> {
                        if (node.hasClass("invisible")) {
                            skip = true
                        }
                    }
                }
            }
        }

        override fun tail(node: Node, depth: Int) {
            if (node is Element) {
                when (node.tagName()) {
                    "a" -> {
                        if (popQueue.isNotEmpty()) {
                            spanBuilder.pop(popQueue.removeLast())
                        }
                    }

                    "span" -> {
                        skip = false
                    }
                }
            }
        }
    }
}

private val EMOJI_CODE_PATTERN = (":(\\w+):").toRegex()

internal fun AnnotatedString.Builder.appendWithEmoji(
    text: String,
    emojis: Map<String, Emoji>,
) {
    if (text.isEmpty()) {
        return
    }
    if (emojis.isEmpty()) {
        append(text)
        return
    }
    val results = EMOJI_CODE_PATTERN.findAll(text)

    var index = 0
    results.iterator().forEach {
        if (it.range.first > index) {
            append(text.substring(index, it.range.first))
        }

        val emojiCode = it.groups[1]?.value
        if (emojiCode != null) {
            val emoji = emojis[emojiCode]
            if (emoji != null) {
                appendInlineContent("emoji", ":${emoji.shortcode}:")
            } else {
                append(it.value)
            }
        } else {
            append(it.value)
        }
        index = it.range.last + 1
    }
    if (index < text.length) {
        append(text.substring(index))
    }
}
