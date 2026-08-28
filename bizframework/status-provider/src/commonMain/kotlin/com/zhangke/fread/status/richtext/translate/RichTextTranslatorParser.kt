package com.zhangke.fread.status.richtext.translate

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import com.fleeksoft.ksoup.select.NodeVisitor
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.FacetFeatureUnion
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.MastodonEmojiUtils
import com.zhangke.fread.status.richtext.OnLinkTargetClick
import com.zhangke.fread.status.richtext.model.RichLinkTarget
import com.zhangke.fread.status.richtext.parser.HtmlParser.QUOTE_INLINE_CLASS
import com.zhangke.fread.status.richtext.parser.appendEmoji
import com.zhangke.fread.status.richtext.parser.buildLinkAnnotation
import com.zhangke.fread.status.richtext.parser.hasNextNonBlankSibling
import kotlinx.serialization.Serializable
import kotlin.math.max

object RichTextTranslatorParser {

    fun parseHtml(
        html: String,
        emojis: List<Emoji> = emptyList(),
        mentions: List<Mention> = emptyList(),
        hashTags: List<HashtagInStatus> = emptyList(),
    ): List<TranslatorBlock> {
        val blockList = mutableListOf<TranslatorBlock>()
        Ksoup.parseBodyFragment(html)
            .body()
            .traverse(
                TranslatorParseVisitor(
                    blockList = blockList,
                    emojis = emojis.associateBy { it.shortcode },
                    mentions = mentions,
                    hashTags = hashTags.map { it.copy(name = it.name.lowercase()) },
                )
            )
        return blockList
    }

    fun parseFacet(
        text: String,
        facets: List<Facet>,
    ): List<TranslatorBlock> {
        if (text.isEmpty()) return emptyList()
        if (facets.isEmpty()) return listOf(TranslatorBlock.PlainTextBlock(text))

        val textBytes = text.encodeToByteArray()
        val blockList = mutableListOf<TranslatorBlock>()
        var currentByteOffset = 0

        for (facet in facets.mergeOverlapFacet()) {
            val start = facet.byteStart.toIntOrNull() ?: continue
            val end = facet.byteEnd.toIntOrNull() ?: continue
            if (start !in currentByteOffset..<end || end > textBytes.size) continue

            val plainText = textBytes.decodeRangeOrNull(currentByteOffset, start) ?: continue
            val facetText = textBytes.decodeRangeOrNull(start, end) ?: continue
            blockList.appendPlainText(plainText)

            val linkTarget = facet.features.firstOrNull()?.toRichLinkTarget()
            if (linkTarget != null) {
                blockList.add(
                    TranslatorBlock.LinkBlock(
                        target = linkTarget,
                        content = facetText,
                    ),
                )
            } else {
                blockList.appendPlainText(facetText)
            }
            currentByteOffset = end
        }

        blockList.appendPlainText(
            textBytes.decodeRangeOrNull(currentByteOffset, textBytes.size).orEmpty(),
        )
        return blockList
    }

    private fun Long.toIntOrNull(): Int? {
        return if (this in 0..Int.MAX_VALUE.toLong()) toInt() else null
    }

    private fun ByteArray.decodeRangeOrNull(start: Int, end: Int): String? {
        return runCatching {
            decodeToString(
                startIndex = start,
                endIndex = end,
                throwOnInvalidSequence = true,
            )
        }.getOrNull()
    }

    private fun MutableList<TranslatorBlock>.appendPlainText(text: String) {
        if (text.isEmpty()) return
        val lastBlock = lastOrNull()
        if (lastBlock is TranslatorBlock.PlainTextBlock) {
            this[lastIndex] = lastBlock.copy(text = lastBlock.text + text)
        } else {
            add(TranslatorBlock.PlainTextBlock(text))
        }
    }

    private fun FacetFeatureUnion.toRichLinkTarget(): RichLinkTarget {
        return when (this) {
            is FacetFeatureUnion.Mention -> RichLinkTarget.MentionDidTarget(did)
            is FacetFeatureUnion.Link -> RichLinkTarget.UrlTarget(uri)
            is FacetFeatureUnion.Tag -> RichLinkTarget.MaybeHashtagTarget(tag)
        }
    }

    internal fun List<Facet>.mergeOverlapFacet(): List<Facet> {
        if (this.isEmpty()) return this
        val sortedFacet = this.sortedBy { it.byteStart }
        val mergedList = mutableListOf<Facet>()

        var longestFacet = sortedFacet.first()
        var overlapGroupStart = longestFacet.byteStart
        var overlapGroupEnd = longestFacet.byteEnd

        for (index in 1 until sortedFacet.size) {
            val currentFacet = sortedFacet[index]
            val overlapsGroup = currentFacet.byteStart < overlapGroupEnd &&
                    currentFacet.byteEnd > overlapGroupStart
            if (overlapsGroup) {
                overlapGroupEnd = max(overlapGroupEnd, currentFacet.byteEnd)
                if (currentFacet.length > longestFacet.length) {
                    longestFacet = currentFacet
                }
            } else {
                mergedList.add(longestFacet)
                longestFacet = currentFacet
                overlapGroupStart = currentFacet.byteStart
                overlapGroupEnd = currentFacet.byteEnd
            }
        }
        mergedList.add(longestFacet)
        return mergedList
    }

    private val Facet.length: Long
        get() = byteEnd - byteStart

    fun rebuildAnnotationString(
        blockList: List<TranslatorBlock>,
        onLinkTargetClick: OnLinkTargetClick,
    ): AnnotatedString {
        return buildAnnotatedString {
            for (block in blockList) {
                when (block) {
                    is TranslatorBlock.PlainTextBlock -> append(block.text)
                    is TranslatorBlock.EmojiBlock -> appendEmoji(block.emoji)
                    is TranslatorBlock.NewLineBlock -> appendLine()
                    is TranslatorBlock.LinkBlock -> {
                        val index = pushLink(
                            buildLinkAnnotation(
                                linkTarget = block.target,
                                onLinkClick = onLinkTargetClick,
                            )
                        )
                        append(block.content)
                        pop(index)
                    }
                }
            }
        }
    }

    private class TranslatorParseVisitor(
        private val blockList: MutableList<TranslatorBlock>,
        private val emojis: Map<String, Emoji>,
        private val mentions: List<Mention>,
        private val hashTags: List<HashtagInStatus>,
    ) : NodeVisitor {

        private val popQueue = ArrayDeque<LinkContext>()

        private var skip = false
        private var inQuoteInline = false

        override fun head(node: Node, depth: Int) {
            if (skip) return
            if (inQuoteInline) return
            if (node is TextNode) {
                appendText(node.text())
                return
            }
            if (node is Element) {
                when (node.tagName()) {
                    "br" -> appendNewLine()

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
                                LinkContext(element = node, target = linkTarget),
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
                        val context = popQueue.lastOrNull()
                        if (context?.element === node) {
                            popQueue.removeLast()
                            appendBlock(
                                TranslatorBlock.LinkBlock(
                                    target = context.target,
                                    content = context.content.toString(),
                                ),
                            )
                        }
                    }

                    "p" -> {
                        if (node.hasClass(QUOTE_INLINE_CLASS)) {
                            inQuoteInline = false
                        } else if (node.hasNextNonBlankSibling()) {
                            appendNewLine()
                            appendNewLine()
                        }
                    }

                    "span" -> {
                        skip = false
                    }
                }
            }
        }

        private fun appendText(text: String) {
            val linkContext = popQueue.lastOrNull()
            if (linkContext != null) {
                linkContext.content.append(text)
            } else {
                blockList.appendWithEmoji(text, emojis)
            }
        }

        private fun appendNewLine() {
            val linkContext = popQueue.lastOrNull()
            if (linkContext != null) {
                linkContext.content.appendLine()
            } else {
                blockList.add(TranslatorBlock.NewLineBlock)
            }
        }

        private fun appendBlock(block: TranslatorBlock.LinkBlock) {
            val parentLinkContext = popQueue.lastOrNull()
            if (parentLinkContext != null) {
                parentLinkContext.content.append(block.content)
            } else {
                blockList.add(block)
            }
        }

        private fun MutableList<TranslatorBlock>.appendWithEmoji(
            text: String,
            emojis: Map<String, Emoji>,
        ) {
            MastodonEmojiUtils.visiteEmojis(
                text = text,
                emojis = emojis,
                onPlainTextFound = { add(TranslatorBlock.PlainTextBlock(it)) },
                onEmojiFound = { add(TranslatorBlock.EmojiBlock(it)) },
            )
        }

        private data class LinkContext(
            val element: Element,
            val target: RichLinkTarget,
            val content: StringBuilder = StringBuilder(),
        )
    }

}

@Serializable
sealed interface TranslatorBlock {

    @Serializable
    data class PlainTextBlock(val text: String) : TranslatorBlock

    @Serializable
    data class EmojiBlock(val emoji: Emoji) : TranslatorBlock

    @Serializable
    data object NewLineBlock : TranslatorBlock

    @Serializable
    data class LinkBlock(
        val target: RichLinkTarget,
        val content: String,
    ) : TranslatorBlock
}

fun List<TranslatorBlock>.plainTextLength(): Int {
    return filterIsInstance<TranslatorBlock.PlainTextBlock>().sumOf { it.text.length }
}
