package com.zhangke.fread.status.richtext

import androidx.compose.ui.text.AnnotatedString
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.PlatformTransient
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.parser.HtmlParser
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

interface IRichText{

    val originText: String

    var onLinkTargetClick: OnLinkTargetClick

    fun parse(): AnnotatedString
}

@Serializable
class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention>,
    private val hashTags: List<HashtagInStatus>,
    val emojis: List<Emoji>,
    private val parsePossibleHashtag: Boolean = false,
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
            parsePossibleHashtag = parsePossibleHashtag,
            onLinkTargetClick = clickableDelegate,
        ).also {
            richText = it
        }
    }

    companion object {
        val empty by lazy { buildRichText("") }
    }
}
