package com.zhangke.fread.status.richtext.android

import android.text.SpannableStringBuilder
import android.text.Spanned
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.android.span.CustomEmojiSpan
import com.zhangke.fread.status.richtext.android.span.InvisibleSpan
import com.zhangke.fread.status.richtext.android.span.LinkSpan
import com.zhangke.fread.status.richtext.model.RichLinkTarget
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor
import java.util.regex.Pattern

object HtmlParser {

    private val EMOJI_CODE_PATTERN = Pattern.compile(":(\\w+):")

    fun parse(
        document: String,
        emojis: List<Emoji>,
        mentions: List<Mention>,
        hashTag: List<HashtagInStatus>,
        parsePossibleHashtag: Boolean = false,
    ): SpannableStringBuilder {
        val fixedHashTags = hashTag.map { it.copy(name = it.name.lowercase()) }
        val spanBuilder = SpannableStringBuilder()
        Jsoup.parseBodyFragment(document)
            .body()
            .traverse(object : NodeVisitor {

                private val openSpans = mutableListOf<SpanInfo>()

                override fun head(node: Node, depth: Int) {
                    if (node is TextNode) {
                        spanBuilder.append(node.text())
                    } else if (node is Element) {
                        parseElement(
                            spanBuilder = spanBuilder,
                            element = node,
                            openSpans = openSpans,
                            mentions = mentions,
                            hashTags = fixedHashTags,
                            parsePossibleHashtag = parsePossibleHashtag,
                        )
                    }
                }

                override fun tail(node: Node, depth: Int) {
                    if (node !is Element) return
                    if ("p" == node.nodeName()) {
                        if (node.nextSibling() != null) spanBuilder.append("\n\n")
                    } else if (openSpans.isNotEmpty()) {
                        val si = openSpans[openSpans.size - 1]
                        if (si.element === node) {
                            spanBuilder.setSpan(
                                si.span,
                                si.start,
                                spanBuilder.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            openSpans.removeAt(openSpans.size - 1)
                        }
                    }
                }
            })
        if (emojis.isNotEmpty()) {
            parseEmojis(emojis, spanBuilder)
        }
        return spanBuilder
    }

    private fun parseElement(
        spanBuilder: SpannableStringBuilder,
        element: Element,
        openSpans: MutableList<SpanInfo>,
        mentions: List<Mention>,
        hashTags: List<HashtagInStatus>,
        parsePossibleHashtag: Boolean,
    ) {
        when (element.nodeName()) {
            "br" -> {
                spanBuilder.append("\n")
            }

            "a" -> {
                val href = element.attr("href")
                var linkTarget: RichLinkTarget? = null
                if (element.hasClass("hashtag")) {
                    val text = element.text()
                    if (text.startsWith("#")) {
                        if (parsePossibleHashtag) {
                            linkTarget = RichLinkTarget.MaybeHashtagTarget(text.substring(1))
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
                } else if (element.hasClass("mention")) {
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
                    openSpans.add(
                        SpanInfo(
                            span = LinkSpan(linkTarget),
                            start = spanBuilder.length,
                            element = element,
                        )
                    )
                }
            }

            "span" -> {
                if (element.hasClass("invisible")) {
                    openSpans.add(SpanInfo(InvisibleSpan(), spanBuilder.length, element));
                }
            }
        }
    }

    class SpanInfo(var span: Any, var start: Int, var element: Element)

    private fun parseEmojis(
        emojis: List<Emoji>,
        spanBuilder: SpannableStringBuilder,
    ) {
        val codeToEmoji = emojis.associateBy { it.shortcode }
        val matcher = EMOJI_CODE_PATTERN.matcher(spanBuilder)
        var spanCount = 0
        var lastSpan: CustomEmojiSpan? = null
        while (matcher.find()) {
            val emoji: Emoji = matcher.group(1)?.let { codeToEmoji[it] } ?: continue
            spanBuilder.setSpan(
                CustomEmojiSpan(emoji).also { lastSpan = it },
                matcher.start(),
                matcher.end(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spanCount++
        }
        if (spanCount == 1 &&
            spanBuilder.getSpanStart(lastSpan) == 0 &&
            spanBuilder.getSpanEnd(lastSpan) == spanBuilder.length
        ) {
            spanBuilder.append(' ') // To fix line height
        }
    }
}
