package com.zhangke.fread.status.richtext

import androidx.compose.ui.text.AnnotatedString
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.PlatformTransient
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.parser.HtmlParser
import com.zhangke.fread.status.richtext.translate.RichTextTranslatorParser
import com.zhangke.fread.status.richtext.translate.TranslatorBlock
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention> = emptyList(),
    private val hashTags: List<HashtagInStatus> = emptyList(),
    val emojis: List<Emoji> = emptyList(),
    val facets: List<Facet> = emptyList(),
    val type: RichTextType,
    val translationBlockList: List<TranslatorBlock>? = null,
) : PlatformSerializable {

    @PlatformTransient
    private var clickableDelegate: OnLinkTargetClick = { target ->
        onLinkTargetClick?.invoke(target)
    }

    @PlatformTransient
    var onLinkTargetClick: OnLinkTargetClick? = null

    @Contextual
    @PlatformTransient
    private var richText: AnnotatedString? = null

    fun parse(): AnnotatedString {
        richText?.let { return it }
        if (!translationBlockList.isNullOrEmpty()) {
            return RichTextTranslatorParser.rebuildAnnotationString(
                blockList = translationBlockList,
                onLinkTargetClick = clickableDelegate,
            ).also { richText = it }
        }
        return HtmlParser.parse(
            document = document,
            type = type,
            emojis = emojis,
            mentions = mentions,
            hashTags = hashTags,
            facets = facets,
            onLinkTargetClick = clickableDelegate,
        ).also {
            richText = it
        }
    }

    companion object {

        val empty by lazy { buildRichText("") }

        fun create(blocks: List<TranslatorBlock>): RichText {
            return RichText(
                document = "",
                type = RichTextType.PLAINTEXT,
                translationBlockList = blocks,
            )
        }
    }
}

@Serializable
enum class RichTextType {

    HTML,
    PLAINTEXT,
    UNKNOWN,
}
