package com.zhangke.fread.status.richtext

import androidx.compose.ui.text.AnnotatedString
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.PlatformTransient
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.parser.HtmlParser
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
        return HtmlParser.parse(
            document = document,
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
    }
}
