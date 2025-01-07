package com.zhangke.fread.status.richtext.parser

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import com.fleeksoft.ksoup.select.NodeVisitor
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.OnLinkTargetClick
import com.zhangke.fread.status.richtext.model.RichLinkTarget

private val linkColor = Color(0xFF0000FF)

object HtmlParser {

    private val EMOJI_CODE_PATTERN = (":(\\w+):").toRegex()

    fun parse(
        document: String,
        emojis: List<Emoji>,
        mentions: List<Mention>,
        hashTags: List<HashtagInStatus>,
        onLinkTargetClick: OnLinkTargetClick,
        parsePossibleHashtag: Boolean = false,
    ): AnnotatedString {
        return buildAnnotatedString {
            //<p>Hey hey it&#39;s the last <a href="https://mastodon.social/tags/AlternateFridayMusic" class="mention hashtag" rel="tag">#<span>AlternateFridayMusic</span></a> for  2024. <br /><a href="https://mastodon.social/tags/Zen" class="mention hashtag" rel="tag">#<span>Zen</span></a> &amp; the art of posting songs to random hashtags: Jump onboard!</p><p><a href="https://www.youtube.com/watch?v=0Ijoxcwg1Lo" target="_blank" rel="nofollow noopener" translate="no"><span class="invisible">https://www.</span><span class="">youtube.com/watch?v=0Ijoxcwg1Lo</span><span class="invisible"></span></a></p>

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
                appendText(node.text())
                return
            }
            if (node is Element) {
                when (node.tagName()) {
                    "br" -> {
                        spanBuilder.append("\n")
                    }

                    "a" -> {
                        val element = node
                        val href = element.attr("href")
                        var linkTarget: RichLinkTarget? = null
                        if (element.hasClass("hashtag")) {
                            val text = element.text()
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
                            popQueue.addLast(
                                spanBuilder.pushLink(
                                    LinkAnnotation.Clickable(
                                        tag = when (linkTarget) {
                                            is RichLinkTarget.UrlTarget -> linkTarget.url
                                            is RichLinkTarget.MentionTarget -> linkTarget.mention.id
                                            is RichLinkTarget.HashtagTarget -> linkTarget.hashtag.name
                                            is RichLinkTarget.MaybeHashtagTarget -> linkTarget.hashtag
                                        },
                                        linkInteractionListener = {
                                            onLinkTargetClick(linkTarget)
                                        }
                                    )
                                )
                            )
                            popQueue.addLast(
                                spanBuilder.pushStyle(
                                    SpanStyle(
                                        color = linkColor,
                                        textDecoration = TextDecoration.Underline,
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

        private fun appendText(text: String) {
            if (text.isEmpty()) {
                return
            }
            if (emojis.isEmpty()) {
                spanBuilder.append(text)
                return
            }
            val results = EMOJI_CODE_PATTERN.findAll(spanBuilder.toString())

            var index = 0
            results.iterator().forEach {
                if (it.range.first > index) {
                    spanBuilder.append(text.substring(index, it.range.first))
                }
                val emoji = emojis[it.value]
                if (emoji != null) {
                    spanBuilder.appendInlineContent("emoji", emoji.url)
                } else {
                    spanBuilder.append(it.value)
                }
                index = it.range.last + 1
            }
            if (index < text.length) {
                spanBuilder.append(text.substring(index))
            }
        }
    }
}

