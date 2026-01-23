package com.zhangke.fread.status.richtext.parser

import androidx.compose.foundation.text.appendInlineContent
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
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.OnLinkTargetClick
import com.zhangke.fread.status.richtext.RichTextType
import com.zhangke.fread.status.richtext.model.RichLinkTarget

object HtmlParser {

    private const val QUOTE_INLINE_CLASS = "quote-inline"
    private val simpleHtmlRegex = "<[^>]+>".toRegex()

    fun parse(
        document: String,
        type: RichTextType,
        emojis: List<Emoji> = emptyList(),
        mentions: List<Mention> = emptyList(),
        hashTags: List<HashtagInStatus> = emptyList(),
        facets: List<Facet> = emptyList(),
        onLinkTargetClick: OnLinkTargetClick = {},
    ): AnnotatedString {
        var isPlaintext = type == RichTextType.PLAINTEXT || facets.isNotEmpty()
        if (type == RichTextType.UNKNOWN) {
            if (!simpleHtmlRegex.containsMatchIn(document)) {
                isPlaintext = true
            }
        }
        if (isPlaintext) {
            return PlaintextParser.parse(document, facets, onLinkTargetClick)
        }
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
                    )
                )
        }
    }

    private class ParseVisitor(
        private val spanBuilder: AnnotatedString.Builder,
        private val emojis: Map<String, Emoji>,
        private val mentions: List<Mention>,
        private val hashTags: List<HashtagInStatus>,
        private val onLinkTargetClick: OnLinkTargetClick,
    ) : NodeVisitor {

        private val popQueue = ArrayDeque<Int>()

        private var skip = false
        private var inQuoteInline = false

        override fun head(node: Node, depth: Int) {
            if (skip) return
            if (inQuoteInline) return
            if (node is TextNode) {
                spanBuilder.appendWithEmoji(node.text(), emojis)
                return
            }
            if (node is Element) {
                when (node.tagName()) {
                    "br" -> spanBuilder.appendLine()

                    "p" -> {
                        if (node.hasClass(QUOTE_INLINE_CLASS)) {
                            inQuoteInline = true
                        }
                    }

                    "a" -> {
                        val href = node.attr("href")
                        var linkTarget: RichLinkTarget? = null
                        if (node.hasClass("hashtag")) {
                            val text = node.text()
                            if (text.startsWith("#")) {
                                val hashtagText = text.substring(1).lowercase()
                                val hashTag = hashTags.firstOrNull { it.name == hashtagText }
                                linkTarget = if (hashTag != null) {
                                    RichLinkTarget.HashtagTarget(hashTag)
                                } else {
                                    RichLinkTarget.MaybeHashtagTarget(text.substring(1))
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

                    "p" -> {
                        if (node.hasClass(QUOTE_INLINE_CLASS)) {
                            inQuoteInline = false
                        } else if (node.hasNextNonBlankSibling()) {
                            spanBuilder.appendParagraphBreak()
                        }
                    }

                    "span" -> {
                        skip = false
                    }
                }
            }
        }
    }

    fun parseToPlainText(document: String): String {
        return buildString {
            Ksoup.parseBodyFragment(document)
                .body()
                .traverse(ParseToPlainVisitor(this))
        }
    }

    class ParseToPlainVisitor(private val builder: StringBuilder) : NodeVisitor {

        private var inQuoteInline = false

        private fun Element?.isMention(): Boolean {
            if (this == null) return false
            if (hasClass("hashtag")) return false
            if (hasClass("mention")) return true
            return parent().isMention()
        }

        private fun Element?.mentionHref(): String? {
            if (this == null) return null
            if (hasClass("mention")) {
                return this.attr("href")
            }
            return parent().mentionHref()
        }

        override fun head(node: Node, depth: Int) {
            if (inQuoteInline) return
            if (node is Element) {
                if (node.tagName() == "p" && node.hasClass(QUOTE_INLINE_CLASS)) {
                    inQuoteInline = true
                    return
                }
                if (node.tagName() == "br") {
                    builder.appendLine()
                }
                if (node.isMention()) {
                    if (node.tagName() != "a") return
                    val text = node.text()
                    val href = node.mentionHref()
                    builder.append(buildMentionText(text, href))
                    return
                }
            }
            if (node is TextNode) {
                if ((node.parent() as? Element)?.isMention() == true) return
                builder.append(node.text())
            }
        }

        override fun tail(node: Node, depth: Int) {
            if (node is Element && node.tagName() == "p") {
                if (node.hasClass(QUOTE_INLINE_CLASS)) {
                    inQuoteInline = false
                } else if (node.hasNextNonBlankSibling()) {
                    builder.appendParagraphBreak()
                }
            }
        }

        private fun buildMentionText(text: String, href: String?): String {
            if (href.isNullOrBlank()) return text
            val url = SimpleUri.parse(href) ?: return text
            val textAsWebFinger = WebFinger.create(text)
            if (textAsWebFinger != null) return text
            return "$text@${url.host}"
        }
    }

}

private val EMOJI_CODE_PATTERN = (":(\\w+):").toRegex()

private fun AnnotatedString.Builder.appendParagraphBreak() {
    appendLine()
    appendLine()
}

private fun StringBuilder.appendParagraphBreak() {
    appendLine()
    appendLine()
}

private fun Node.hasNextNonBlankSibling(): Boolean {
    var sibling = nextSibling()
    while (sibling != null) {
        when (sibling) {
            is TextNode -> {
                if (sibling.text().isNotBlank()) return true
            }
            is Element -> return true
        }
        sibling = sibling.nextSibling()
    }
    return false
}

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
